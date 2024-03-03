package org.team9432.robot


import edu.wpi.first.math.proto.Controller
import org.team9432.lib.commandbased.commands.*
import org.team9432.lib.commandbased.input.KXboxController
import org.team9432.lib.wpilib.ChassisSpeeds
import org.team9432.robot.auto.commands.testAuto
import org.team9432.robot.commands.drivetrain.DriveStraightToPosition
import org.team9432.robot.commands.drivetrain.FieldOrientedDrive
import org.team9432.robot.commands.drivetrain.MobileSpeakerAlign
import org.team9432.robot.commands.drivetrain.StaticSpeakerAlign
import org.team9432.robot.commands.hopper.MoveToSide
import org.team9432.robot.commands.intake.AlignNote
import org.team9432.robot.commands.intake.IntakeToBeambreak
import org.team9432.robot.commands.shooter.ShootStatic
import org.team9432.robot.subsystems.amp.Amp
import org.team9432.robot.subsystems.beambreaks.BeambreakIOSim
import org.team9432.robot.subsystems.beambreaks.Beambreaks
import org.team9432.robot.subsystems.climber.LeftClimber
import org.team9432.robot.subsystems.climber.RightClimber
import org.team9432.robot.subsystems.drivetrain.Drivetrain
import org.team9432.robot.subsystems.hood.Hood
import org.team9432.robot.subsystems.hopper.Hopper
import org.team9432.robot.subsystems.intake.Intake
import org.team9432.robot.subsystems.limelight.Limelight
import org.team9432.robot.subsystems.shooter.CommandShooter
import org.team9432.robot.subsystems.shooter.Shooter

object Controls {
    private val controller = KXboxController(0, squareJoysticks = true, joystickDeadband = 0.075)

    val xSpeedSupplier = { -controller.leftY }
    var ySpeedSupplier = { -controller.leftX }
    var angleSupplier = { -controller.rightX }

    init {
        Drivetrain.defaultCommand = FieldOrientedDrive()

        // Pretend to get a note after 2 seconds in sim
        controller.leftBumper.whileTrue(IntakeToBeambreak().afterSimDelay(2.0) {
            BeambreakIOSim.setNoteInIntake(RobotState.getMovementDirection(), true)
            BeambreakIOSim.setNoteInCenter(true)
        }).onFalse(AlignNote().withTimeout(2.0))

        controller.y.onTrue(InstantCommand {
            BeambreakIOSim.setNoteInIntakeAmpSide(false)
            BeambreakIOSim.setNoteInIntakeSpeakerSide(false)
            BeambreakIOSim.setNoteInHopperAmpSide(false)
            BeambreakIOSim.setNoteInHopperSpeakerSide(false)
            BeambreakIOSim.setNoteInCenter(false)
            RobotState.notePosition = RobotState.NotePosition.NONE
        })

        controller.x.onTrue(MoveToSide(MechanismSide.AMP).withTimeout(3.0))
        controller.b.onTrue(MoveToSide(MechanismSide.SPEAKER).withTimeout(3.0))

//        controller.x.onTrue(SuppliedCommand(Drivetrain) { DriveStraightToPosition(FieldConstants.ampPose) })
//        controller.b.onTrue(InstantCommand(Drivetrain) {})

        controller.rightTrigger.onTrue(ShootStatic(6000.0, 6000.0))
        controller.leftTrigger.onTrue(ShootStatic(2000.0, 2000.0))

//        controller.a.toggleOnTrue(
//            SimpleCommand(
//                execute = { Shooter.setVoltage(controller.rightTriggerAxisRaw * 1.0, 0.0)},
//                end = {Shooter.stop()}
//            )
//        )

        controller.a.onTrue(InstantCommand { Drivetrain.resetGyro() })

//        controller.start.onTrue(CommandShooter.setSpeed(2000.0, 0.0))
//        controller.back.onTrue(CommandShooter.setSpeed(0.0, 0.0))

       // controller.start.onTrue(testAuto)

        controller.start.onTrue(InstantCommand(LeftClimber, RightClimber) {
            LeftClimber.setVoltage(6.0)
            RightClimber.setVoltage(6.0)
        }).onFalse(InstantCommand(LeftClimber, RightClimber) {
            LeftClimber.stop()
            RightClimber.stop()
        })

        controller.back.onTrue(InstantCommand(LeftClimber, RightClimber) {
            LeftClimber.setVoltage(-6.0)
            RightClimber.setVoltage(-6.0)
        }).onFalse(InstantCommand(LeftClimber, RightClimber) {
            LeftClimber.stop()
            RightClimber.stop()
        })

//        controller.leftBumper.onTrue(InstantCommand { Shooter.setVoltage(0.70, 0.70) }).onFalse(InstantCommand { Shooter.setVoltage(0.0, 0.0) })

//        controller.rightTrigger.onTrue(StaticSpeakerAlign()).onFalse(FieldOrientedDrive())
//        controller.leftTrigger.onTrue(MobileSpeakerAlign()).onFalse(FieldOrientedDrive())
    }

    fun getDrivetrainSpeeds(): ChassisSpeeds {
        val maxSpeedMetersPerSecond = if (controller.rightBumper.asBoolean) 6.0 else 2.5
        val xSpeed = xSpeedSupplier.invoke() * maxSpeedMetersPerSecond
        val ySpeed = ySpeedSupplier.invoke() * maxSpeedMetersPerSecond
        val radiansPerSecond = Math.toRadians(angleSupplier.invoke() * 360.0)
        return ChassisSpeeds(xSpeed, ySpeed, radiansPerSecond)
    }
}
