package org.team9432.robot


import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.lib.commandbased.commands.afterSimDelay
import org.team9432.lib.commandbased.input.KXboxController
import org.team9432.robot.commands.MoveToSide
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
    private val controller = KXboxController(0, squareJoysticks = true, joystickDeadband = 0.0)

    init {
        Drivetrain
        Hopper
        Intake
        Hood
        Shooter
        Amp
        Beambreaks

        Drivetrain.defaultCommand = Drivetrain.fieldOrientedDriveCommand({ -controller.leftY }, { -controller.leftX }, { -controller.rightX }, maxSpeedMetersPerSecond = 3.5)
        controller.rightBumper.whileTrue(Drivetrain.fieldOrientedDriveCommand({ -controller.leftY }, { -controller.leftX }, { -controller.rightX }, maxSpeedMetersPerSecond = 6.0))

        // Pretend to get a note after 2 seconds in sim
        controller.rightTrigger.whileTrue(IntakeToBeambreak().afterSimDelay(2.0) {
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

        controller.leftBumper.onTrue(InstantCommand { Shooter.setVoltage(0.70, 0.70) }).onFalse(InstantCommand { Shooter.setVoltage(0.0, 0.0) })
    }
}
