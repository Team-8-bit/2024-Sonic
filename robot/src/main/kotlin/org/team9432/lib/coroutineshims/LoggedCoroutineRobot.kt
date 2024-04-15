//package org.team9432.lib
package org.littletonrobotics.junction


import edu.wpi.first.hal.DriverStationJNI
import kotlinx.coroutines.delay
import org.team9432.lib.coroutineshims.IterativeRobotBase

const val LOOP_PERIOD_SECS = 0.02

/*
 * LoggedRobot implements the IterativeRobotBase robot program framework. This is a modified version that uses coroutines to run the periodic loop.
 *
 * The LoggedRobot class is intended to be subclassed by a user creating a robot program, and will call all required AdvantageKit periodic methods.
 */
open class LoggedCoroutineRobot: IterativeRobotBase() {
    private val periodUs = (LOOP_PERIOD_SECS * 1000000).toLong()
    private var nextCycleUs: Long = 0
    private var useTiming = true

    private var isRunning = true

    /** Provide an alternate "main loop" via startCompetition().  */
    override suspend fun startCompetition() {
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
        while (isRunning) {
            if (useTiming) {
                val currentTimeUs = Logger.getRealTimestamp()
                if (nextCycleUs < currentTimeUs) {
                    // Loop overrun, start next cycle immediately
                    nextCycleUs = currentTimeUs
                } else {
                    // Wait before next cycle
                    delay((nextCycleUs - currentTimeUs) / 1000)
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
        isRunning = false
    }

    /** Sets whether to use standard timing or run as fast as possible.  */
    fun setUseTiming(useTiming: Boolean) {
        this.useTiming = useTiming
    }
}