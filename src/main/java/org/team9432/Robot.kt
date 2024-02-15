package org.team9432

import edu.wpi.first.wpilibj.PowerDistribution
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.CommandScheduler
import org.littletonrobotics.junction.LogFileUtil
import org.littletonrobotics.junction.LoggedRobot
import org.littletonrobotics.junction.Logger
import org.littletonrobotics.junction.networktables.NT4Publisher
import org.littletonrobotics.junction.wpilog.WPILOGReader
import org.littletonrobotics.junction.wpilog.WPILOGWriter


/**
 * The VM is configured to automatically run this object (which basically functions as a singleton class),
 * and to call the functions corresponding to each mode, as described in the TimedRobot documentation.
 * This is written as an object rather than a class since there should only ever be a single instance, and
 * it cannot take any constructor arguments. This makes it a natural fit to be an object in Kotlin.
 *
 * If you change the name of this object or its package after creating this project, you must also update
 * the `Main.kt` file in the project. (If you use the IDE's Rename or Move refactorings when renaming the
 * object or package, it will get changed everywhere.)
 */
object Robot: LoggedRobot() {
    private var autonomousCommand: Command? = null

    override fun robotInit() {
        // Access the RobotContainer object so that it is initialized. This will perform all our
        // button bindings, and put our autonomous chooser on the dashboard.
        Logger.recordMetadata("ProjectName", "2024 - Sonic") // Set a metadata value

        if (isReal()) {
            Logger.addDataReceiver(WPILOGWriter()) // Log to a USB stick ("/U/logs")
            Logger.addDataReceiver(NT4Publisher()) // Publish data to NetworkTables
            PowerDistribution(1, PowerDistribution.ModuleType.kRev) // Enables power distribution logging
        } else {
            setUseTiming(false) // Run as fast as possible
            val logPath = LogFileUtil.findReplayLog() // Pull the replay log from AdvantageScope (or prompt the user)
            Logger.setReplaySource(WPILOGReader(logPath)) // Read replay log
            Logger.addDataReceiver(WPILOGWriter(LogFileUtil.addPathSuffix(logPath, "_sim"))) // Save outputs to a new log
        }


        // Logger.disableDeterministicTimestamps() // See "Deterministic Timestamps" in the "Understanding Data Flow" page
        Logger.start()
        RobotContainer
    }


    override fun robotPeriodic() {
        CommandScheduler.getInstance().run()
    }

    override fun disabledInit() {

    }

    override fun disabledPeriodic() {

    }

    override fun autonomousInit() {
        autonomousCommand = RobotContainer.getAutonomousCommand()
        autonomousCommand?.schedule()
    }

    override fun autonomousPeriodic() {

    }

    override fun teleopInit() {
        autonomousCommand?.cancel()
    }

    /** This method is called periodically during operator control.  */
    override fun teleopPeriodic() {

    }

    override fun testInit() {
        // Cancels all running commands at the start of test mode.
        CommandScheduler.getInstance().cancelAll()
    }

    override fun testPeriodic() {

    }

    override fun simulationInit() {

    }

    override fun simulationPeriodic() {

    }
}