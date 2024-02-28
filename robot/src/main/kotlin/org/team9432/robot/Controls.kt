package org.team9432.robot


import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.lib.commandbased.commands.afterSimDelay
import org.team9432.lib.commandbased.input.KXboxController
import org.team9432.lib.wpilib.ChassisSpeeds
import org.team9432.robot.commands.hopper.MoveToSide
import org.team9432.robot.auto.commands.testAuto
import org.team9432.robot.commands.drivetrain.FieldOrientedDrive
import org.team9432.robot.commands.drivetrain.MobileSpeakerAlign
import org.team9432.robot.commands.drivetrain.StaticSpeakerAlign
import org.team9432.robot.commands.intake.AlignNote
import org.team9432.robot.commands.intake.IntakeToBeambreak
import org.team9432.robot.subsystems.amp.Amp
import org.team9432.robot.subsystems.beambreaks.BeambreakIOSim
import org.team9432.robot.subsystems.beambreaks.Beambreaks
import org.team9432.robot.subsystems.drivetrain.Drivetrain
import org.team9432.robot.subsystems.hood.Hood
import org.team9432.robot.subsystems.hopper.Hopper
import org.team9432.robot.subsystems.intake.Intake
import org.team9432.robot.subsystems.shooter.Shooter

object Controls {
    private val controller = KXboxController(0, squareJoysticks = true, joystickDeadband = 0.075)

    val xSpeedSupplier = { -controller.leftY }
    var ySpeedSupplier = { -controller.leftX }
    var angleSupplier = { -controller.rightX }

    init {

        Drivetrain
        Hopper
        Intake
        Hood
        Shooter
        Amp
        Beambreaks

        Drivetrain.defaultCommand = FieldOrientedDrive()

        // Pretend to get a note after 2 seconds in sim
        controller.leftBumper.whileTrue(IntakeToBeambreak().afterSimDelay(2.0) {
            BeambreakIOSim.setNoteInIntake(RobotState.getMovementDirection(), true)
            BeambreakIOSim.setNoteInCenter(true)
        }).onFalse(AlignNote())

        controller.y.onTrue(InstantCommand {
            BeambreakIOSim.setNoteInIntakeAmpSide(false)
            BeambreakIOSim.setNoteInIntakeSpeakerSide(false)
            BeambreakIOSim.setNoteInHopperAmpSide(false)
            BeambreakIOSim.setNoteInHopperSpeakerSide(false)
            BeambreakIOSim.setNoteInCenter(false)
            RobotState.notePosition = RobotState.NotePosition.NONE
        })

        controller.x.onTrue(MoveToSide(MechanismSide.AMP))
        controller.b.onTrue(MoveToSide(MechanismSide.SPEAKER))

        controller.a.onTrue(InstantCommand { Drivetrain.resetGyro() })

        controller.start.onTrue(testAuto)

//        controller.leftBumper.onTrue(InstantCommand { Shooter.setVoltage(0.70, 0.70) }).onFalse(InstantCommand { Shooter.setVoltage(0.0, 0.0) })

        controller.rightTrigger.onTrue(StaticSpeakerAlign()).onFalse(FieldOrientedDrive())
        controller.leftTrigger.onTrue(MobileSpeakerAlign()).onFalse(FieldOrientedDrive())
    }

    fun getDrivetrainSpeeds(): ChassisSpeeds {
        val maxSpeedMetersPerSecond = if (controller.rightBumper.asBoolean) 6.0 else 2.5
        val xSpeed = xSpeedSupplier.invoke() * maxSpeedMetersPerSecond
        val ySpeed = ySpeedSupplier.invoke() * maxSpeedMetersPerSecond
        val radiansPerSecond = Math.toRadians(angleSupplier.invoke() * 360.0)
        return ChassisSpeeds(xSpeed, ySpeed, radiansPerSecond)
    }
}
