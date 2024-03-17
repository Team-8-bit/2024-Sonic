package org.team9432

import edu.wpi.first.hal.FRCNetComm
import edu.wpi.first.hal.HAL
import edu.wpi.first.math.geometry.*
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
import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.lib.commandbased.commands.ParallelCommand
import org.team9432.lib.commandbased.commands.SequentialCommand
import org.team9432.lib.commandbased.commands.withTimeout
import org.team9432.lib.util.PoseUtil
import org.team9432.robot.oi.Controls
import org.team9432.robot.RobotState
import org.team9432.robot.auto.AutoBuilder
import org.team9432.robot.auto.AutoChooser
import org.team9432.robot.auto.commands.PullFromSpeakerShooter
import org.team9432.robot.commands.CommandConstants
import org.team9432.robot.commands.hood.HoodAimAtSpeaker
import org.team9432.robot.commands.stop
import org.team9432.robot.led.LEDState
import org.team9432.robot.led.LEDs
import org.team9432.robot.sensors.beambreaks.Beambreaks
import org.team9432.robot.sensors.gyro.Gyro
import org.team9432.robot.sensors.vision.Vision
import org.team9432.robot.subsystems.RobotPosition
import org.team9432.robot.subsystems.amp.Amp
import org.team9432.robot.subsystems.climber.LeftClimber
import org.team9432.robot.subsystems.climber.RightClimber
import org.team9432.robot.subsystems.drivetrain.Drivetrain
import org.team9432.robot.subsystems.hood.Hood
import org.team9432.robot.subsystems.hopper.Hopper
import org.team9432.robot.subsystems.intake.Intake
import org.team9432.robot.subsystems.shooter.Shooter
import kotlin.jvm.optionals.getOrNull

val LOOP_PERIOD_SECS = Robot.period

object Robot: LoggedRobot() {
    val mode = if (isReal()) Mode.REAL else Mode.SIM

    var alliance: Alliance? = null

    val coordinateFlip get() = if (alliance == Alliance.Blue) 1 else -1
    val rotationOffset: Rotation2d get() = if (alliance == Alliance.Blue) Rotation2d() else Rotation2d.fromDegrees(180.0)

    fun Pose2d.applyFlip() = if (alliance == Alliance.Blue) this else PoseUtil.flip(this)

    override fun robotInit() = Init.initRobot()

    override fun robotPeriodic() {
        KCommandScheduler.run()
        RobotState.log()

        LEDState.updateState()

        Logger.recordOutput("Drive/FuturePose", RobotPosition.getFuturePose(CommandConstants.SHOOT_ON_MOVE_SECS))
        Logger.recordOutput("SpeakerSide", RobotPosition.getSpeakerSide())

        DriverStation.getAlliance().getOrNull()?.let { alliance = it }
    }

    override fun disabledInit() {
        KCommandScheduler.cancelAll()
    }

    override fun autonomousInit() {
        ParallelCommand(
            AutoChooser.getCommand(),
            HoodAimAtSpeaker()
        ).schedule()
    }

    override fun teleopInit() {
        stop()

        if (RobotState.noteInSpeakerSideHopperBeambreak()) {
            SequentialCommand(
                PullFromSpeakerShooter(),
                InstantCommand { RobotState.hasRemainingAutoNote = true }
            ).withTimeout(0.75).schedule()
        }
    }

    enum class Mode {
        REAL, SIM, REPLAY
    }
}