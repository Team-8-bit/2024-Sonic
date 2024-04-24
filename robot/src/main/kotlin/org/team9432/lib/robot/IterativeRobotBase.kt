package org.team9432.lib.robot

import edu.wpi.first.hal.DriverStationJNI
import edu.wpi.first.hal.HAL
import edu.wpi.first.networktables.NetworkTableInstance
import edu.wpi.first.wpilibj.DSControlWord
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.Watchdog
import edu.wpi.first.wpilibj.livewindow.LiveWindow
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import org.littletonrobotics.junction.LOOP_PERIOD
import org.team9432.lib.unit.inSeconds

/**
 * IterativeRobotBase implements a specific type of robot program framework, extending the RobotBase class.
 *
 * The IterativeRobotBase class does not implement startCompetition(), so it should not be used by teams directly.
 *
 * This class provides the various init(), periodic(), and exit() functions which are called by the main loop in startCompetition(), at the appropriate times.
 */
abstract class IterativeRobotBase protected constructor(): RobotBase() {
    private enum class Mode {
        DISABLED,
        AUTONOMOUS,
        TELEOP,
        TEST
    }

    private val dsStatus = DSControlWord()
    private val watchdog = Watchdog(LOOP_PERIOD.inSeconds) { DriverStation.reportWarning("Loop time of " + LOOP_PERIOD.inSeconds + "s overrun\n", false) }

    private var lastMode: Mode? = null

    /** Provide an alternate "main loop" via startCompetition().  */
    abstract override fun startCompetition()

    protected fun loopFunc() {
        DriverStation.refreshData()
        watchdog.reset()

        dsStatus.refresh()

        // Get current mode
        val mode = when {
            dsStatus.isDisabled -> Mode.DISABLED
            dsStatus.isAutonomous -> Mode.AUTONOMOUS
            dsStatus.isTeleop -> Mode.TELEOP
            dsStatus.isTest -> Mode.TEST
            else -> null
        }

        // If mode changed, call mode exit and entry functions
        if (lastMode != mode) {
            // Call last mode's exit function
            when (lastMode) {
                Mode.DISABLED -> disabledExit()
                Mode.AUTONOMOUS -> autonomousExit()
                Mode.TELEOP -> teleopExit()
                Mode.TEST -> testExit()
                else -> {}
            }

            // Call current mode's entry function
            when (mode) {
                Mode.DISABLED -> {
                    disabledInit()
                    watchdog.addEpoch("disabledInit()")
                }

                Mode.AUTONOMOUS -> {
                    autonomousInit()
                    watchdog.addEpoch("autonomousInit()")
                }

                Mode.TELEOP -> {
                    teleopInit()
                    watchdog.addEpoch("teleopInit()")
                }

                Mode.TEST -> {
                    testInit()
                    watchdog.addEpoch("testInit()")
                }

                else -> {}
            }

            lastMode = mode
        }

        // Call the appropriate function depending upon the current robot mode
        when (mode) {
            Mode.DISABLED -> {
                DriverStationJNI.observeUserProgramDisabled()
                disabledPeriodic()
                watchdog.addEpoch("disabledPeriodic()")
            }

            Mode.AUTONOMOUS -> {
                DriverStationJNI.observeUserProgramAutonomous()
                autonomousPeriodic()
                watchdog.addEpoch("autonomousPeriodic()")
            }

            Mode.TELEOP -> {
                DriverStationJNI.observeUserProgramTeleop()
                teleopPeriodic()
                watchdog.addEpoch("teleopPeriodic()")
            }

            Mode.TEST -> {
                DriverStationJNI.observeUserProgramTest()
                testPeriodic()
                watchdog.addEpoch("testPeriodic()")
            }

            else -> {}
        }

        robotPeriodic()
        watchdog.addEpoch("robotPeriodic()")

        SmartDashboard.updateValues()
        watchdog.addEpoch("SmartDashboard.updateValues()")
        LiveWindow.updateValues()
        watchdog.addEpoch("LiveWindow.updateValues()")
        Shuffleboard.update()
        watchdog.addEpoch("Shuffleboard.update()")

        if (isSimulation) {
            HAL.simPeriodicBefore()
            simulationPeriodic()
            HAL.simPeriodicAfter()
            watchdog.addEpoch("simulationPeriodic()")
        }

        watchdog.disable()

        // Flush NetworkTables
        NetworkTableInstance.getDefault().flushLocal()

        // Warn on loop time overruns
        if (watchdog.isExpired) watchdog.printEpochs()
    }

    /* ----------- Initialization Functions ----------- */

    /**
     * Robot-wide initialization code should go here.
     *
     *
     * Users should override this method for default Robot-wide initialization which will be called
     * when the robot is first powered on. It will be called exactly one time.
     *
     *
     * Warning: the Driver Station "Robot Code" light and FMS "Robot Ready" indicators will be off
     * until RobotInit() exits. Code in RobotInit() that waits for enable will cause the robot to
     * never indicate that the code is ready, causing the robot to be bypassed in a match.
     */
    open fun robotInit() {}

    /** Override this method for default Robot-wide simulation related initialization which will be called when the robot is first started. It will be called exactly one time after RobotInit is called, only when the robot is in simulation. */
    open fun simulationInit() {}

    /** Override this method for initialization code which will be called each time the robot enters disabled mode. */
    open fun disabledInit() {}

    /** Override this method for initialization code which will be called each time the robot enters autonomous mode. */
    open fun autonomousInit() {}

    /** Override this method for initialization code which will be called each time the robot enters teleop mode. */
    open fun teleopInit() {}

    /** Override this method for initialization code which will be called each time the robot enters test mode. */
    open fun testInit() {}


    /* ----------- Periodic Functions ----------- */

    /** Periodic code for all robot modes should go here.  */
    open fun robotPeriodic() {}

    /**
     * Periodic simulation code should go here.
     *
     * This function is called in a simulated robot after user code executes.
     */
    open fun simulationPeriodic() {}

    /** Override this method for code that will called periodically while the robot is in disabled mode. */
    open fun disabledPeriodic() {}

    /** Override this method for code that will called periodically while the robot is in autonomous mode. */
    open fun autonomousPeriodic() {}

    /** Override this method for code that will called periodically while the robot is in teleop mode. */
    open fun teleopPeriodic() {}

    /** Override this method for code that will called periodically while the robot is in test mode. */
    open fun testPeriodic() {}


    /* ----------- Exit Functions ----------- */

    /** Override this method for code which will be called each time the robot exits disabled mode. */
    open fun disabledExit() {}

    /** Override this method for code which will be called each time the robot exits autonomous mode. */
    open fun autonomousExit() {}

    /** Override this method for code which will be called each time the robot exits teleop mode. */
    open fun teleopExit() {}

    /** Override this method for code which will be called each time the robot exits test mode. */
    open fun testExit() {}
}