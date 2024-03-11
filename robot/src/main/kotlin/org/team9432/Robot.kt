package org.team9432

import edu.wpi.first.hal.FRCNetComm
import edu.wpi.first.hal.HAL
import edu.wpi.first.math.geometry.Pose3d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.geometry.Rotation3d
import edu.wpi.first.math.geometry.Translation3d
import edu.wpi.first.net.PortForwarder
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
import org.team9432.lib.commandbased.commands.SequentialCommand
import org.team9432.robot.AdditionalTriggers
import org.team9432.robot.Controls
import org.team9432.robot.RobotState
import org.team9432.robot.auto.*
import org.team9432.robot.subsystems.amp.Amp
import org.team9432.robot.subsystems.beambreaks.Beambreaks
import org.team9432.robot.subsystems.climber.LeftClimber
import org.team9432.robot.subsystems.climber.RightClimber
import org.team9432.robot.subsystems.drivetrain.Drivetrain
import org.team9432.robot.subsystems.gyro.Gyro
import org.team9432.robot.subsystems.hood.Hood
import org.team9432.robot.subsystems.hopper.Hopper
import org.team9432.robot.subsystems.intake.Intake
import org.team9432.robot.subsystems.led.LEDs
import org.team9432.robot.subsystems.limelight.Limelight
import org.team9432.robot.subsystems.shooter.Shooter
import org.team9432.robot.subsystems.vision.Vision
import kotlin.jvm.optionals.getOrNull

val LOOP_PERIOD_SECS = Robot.period

object Robot: LoggedRobot() {
    val mode = if (isReal()) Mode.REAL else Mode.SIM

    var alliance: Alliance? = null

    override fun robotInit() {
        LEDs

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
            Logger.addDataReceiver(WPILOGWriter()) // Log to a USB stick ("/U/logs")
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
        Logger.recordOutput(
            "Subsystems/Limelight",
            Pose3d(Translation3d(-0.063500, 0.0, 0.420370 + 0.124460), Rotation3d(0.0, 0.0, Math.toRadians(180.0)))
        )

        PortForwarder.add(5800, "photonvision.local", 5800)

        HAL.report(FRCNetComm.tResourceType.kResourceType_Language, FRCNetComm.tInstances.kLanguage_Kotlin)

        Controls
        Vision
        Gyro
        Drivetrain
        Hopper
        Intake
        Hood
        Shooter
        Amp
        Beambreaks
        LeftClimber
        RightClimber
        Limelight
        AdditionalTriggers

      //  AutoBuilder.initDashboard()
    }

    override fun robotPeriodic() {
        KCommandScheduler.run()
        RobotState.log()

        DriverStation.getAlliance().getOrNull()?.let { alliance = it }
    }

    override fun disabledInit() {
        KCommandScheduler.cancelAll()
    }

    override fun autonomousInit() {
        SequentialCommand(
            InitAuto(Rotation2d(Math.PI)),
            StartStageNote(),
            ScoreCenterNote(),
            ExitAuto()
        ).schedule()
    }

    enum class Mode {
        REAL, SIM, REPLAY
    }
}