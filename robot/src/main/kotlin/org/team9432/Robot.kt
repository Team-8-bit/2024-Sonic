package org.team9432

import edu.wpi.first.math.geometry.Pose3d
import edu.wpi.first.math.geometry.Rotation3d
import edu.wpi.first.math.geometry.Translation3d
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.DriverStation.Alliance
import edu.wpi.first.wpilibj.PowerDistribution
import org.littletonrobotics.junction.LogFileUtil
import org.littletonrobotics.junction.LoggedRobot
import org.littletonrobotics.junction.Logger
import org.littletonrobotics.junction.networktables.NT4Publisher
import org.littletonrobotics.junction.wpilog.WPILOGReader
import org.littletonrobotics.junction.wpilog.WPILOGWriter
import org.team9432.lib.commandbased.KCommandScheduler
import org.team9432.robot.Controls
import org.team9432.robot.RobotState
import org.team9432.robot.subsystems.led.LEDs

val LOOP_PERIOD_SECS = Robot.period

object Robot: LoggedRobot() {
    val mode = if (isReal()) Mode.REAL else Mode.SIM

    var alliance: Alliance? = null

    override fun robotInit() {
        Logger.recordMetadata("ProjectName", "2024 - Sonic")
        Logger.recordMetadata("BuildDate", BuildConstants.BUILD_DATE)
        Logger.recordMetadata("GitSHA", BuildConstants.GIT_SHA)
        Logger.recordMetadata("GitDate", BuildConstants.GIT_DATE)
        Logger.recordMetadata("GitBranch", BuildConstants.GIT_BRANCH)
        when (BuildConstants.DIRTY) {
            0 -> Logger.recordMetadata("GitDirty", "All changes committed")
            1 -> Logger.recordMetadata("GitDirty", "Uncomitted changes")
            else -> Logger.recordMetadata("GitDirty", "Unknown")
        }

        if (isReal() || mode == Mode.SIM) {
       //     Logger.addDataReceiver(WPILOGWriter()) // Log to a USB stick ("/U/logs")
            Logger.addDataReceiver(NT4Publisher()) // Publish data to NetworkTables
            PowerDistribution(1, PowerDistribution.ModuleType.kRev) // Enables power distribution logging
        } else if (mode == Mode.REPLAY) {
            setUseTiming(false) // Run as fast as possible
            val logPath = LogFileUtil.findReplayLog() // Pull the replay log from AdvantageScope (or prompt the user)
            Logger.setReplaySource(WPILOGReader(logPath)) // Read replay log
            Logger.addDataReceiver(WPILOGWriter(LogFileUtil.addPathSuffix(logPath, "_sim"))) // Save outputs to a new log
        }

        // Logger.disableDeterministicTimestamps() // See "Deterministic Timestamps" in the "Understanding Data Flow" page
        Logger.start()

        Logger.recordOutput("Subsystems/Climber", Pose3d(Translation3d(0.0, 0.0, 0.0), Rotation3d()))
        Logger.recordOutput("Subsystems/Limelight", Pose3d(Translation3d(-0.063500, 0.0, 0.420370 + 0.124460), Rotation3d(0.0, 0.0, Math.toRadians(180.0))))

        Controls
        LEDs
    }

    override fun robotPeriodic() {
        KCommandScheduler.run()
        RobotState.log()

        if (alliance == null) {
            alliance = DriverStation.getAlliance().orElse(null)
        }
    }

    enum class Mode {
        REAL, SIM, REPLAY
    }
}