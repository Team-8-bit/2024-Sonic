package org.littletonrobotics.junction


import edu.wpi.first.hal.DriverStationJNI
import edu.wpi.first.hal.NotifierJNI
import org.team9432.lib.robot.IterativeRobotBase
import org.team9432.lib.unit.inSeconds
import org.team9432.lib.unit.milliseconds

val LOOP_PERIOD = 20.milliseconds

/*
 * LoggedRobot implements the IterativeRobotBase robot program framework.
 *
 * The LoggedRobot class is intended to be subclassed by a user creating a robot program, and will call all required AdvantageKit periodic methods.
 */
open class LoggedCoroutineRobot: IterativeRobotBase() {
    private val periodUs = (LOOP_PERIOD.inSeconds * 1000000).toLong()
    private val notifier = NotifierJNI.initializeNotifier()
    private var nextCycleUs: Long = 0
    var useTiming = true

    init {
        NotifierJNI.setNotifierName(notifier, "LoggedCoroutineRobot")
    }

    /** Provide an alternate "main loop" via startCompetition().  */
    override fun startCompetition() {
        // Check for invalid AdvantageKit install in sim
        if (isSimulation) {
            CheckInstall.run()
        }

        // Robot init methods
        val initStart = Logger.getRealTimestamp()
        robotInit()
        if (isSimulation) {
            simulationInit()
        }
        val initEnd = Logger.getRealTimestamp()

        // Register auto logged outputs
        AutoLogOutputManager.registerFields(this)

        // Save data from init cycle
        Logger.periodicAfterUser(initEnd - initStart, 0)

        // Tell the DS that the robot is ready to be enabled
        println("********** Robot program startup complete **********")
        DriverStationJNI.observeUserProgramStarting()

        // Loop forever, calling the appropriate mode-dependent function
        while (true) {
            if (useTiming) {
                val currentTimeUs = Logger.getRealTimestamp()
                if (nextCycleUs < currentTimeUs) {
                    // Loop overrun, start next cycle immediately
                    nextCycleUs = currentTimeUs
                } else {
                    // Wait before next cycle
                    NotifierJNI.updateNotifierAlarm(notifier, nextCycleUs)
                    NotifierJNI.waitForNotifierAlarm(notifier)
                }
                nextCycleUs += periodUs
            }

            val periodicBeforeStart = Logger.getRealTimestamp()
            Logger.periodicBeforeUser()
            val userCodeStart = Logger.getRealTimestamp()
            loopFunc()
            val userCodeEnd = Logger.getRealTimestamp()

            Logger.periodicAfterUser(userCodeEnd - userCodeStart, userCodeStart - periodicBeforeStart)
        }
    }

    /** Ends the main loop in startCompetition().  */
    override fun endCompetition() {
        NotifierJNI.stopNotifier(notifier)
    }
}