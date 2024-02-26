package org.team9432.robot


import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.lib.commandbased.commands.ParallelCommand
import org.team9432.lib.commandbased.commands.SimpleCommand
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
        Hopper.defaultCommand = SimpleCommand(execute = { Hopper.setVoltage(0.0) }, requirements = setOf(Hopper))
        Intake.defaultCommand = Intake.stopCommand()

        controller.rightBumper.whileTrue(Drivetrain.fieldOrientedDriveCommand({ -controller.leftY }, { -controller.leftX }, { -controller.rightX }, maxSpeedMetersPerSecond = 6.0))

        controller.rightTrigger.whileTrue(IntakeToBeambreak().afterSimDelay(3.0) {
            // Pretend to get a note after 3 seconds in sim
            if (RobotState.getMovementDirection() == MechanismSide.AMP) BeambreakIOSim.intakeAmpSide = false else BeambreakIOSim.intakeSpeakerSide = false
            BeambreakIOSim.center = false
        }).onFalse(AlignNote())

        controller.y.onTrue(InstantCommand {
            BeambreakIOSim.intakeAmpSide = true
            BeambreakIOSim.intakeSpeakerSide = true
            BeambreakIOSim.hopperAmpSide = true
            BeambreakIOSim.hopperSpeakerSide = true
            BeambreakIOSim.center = true
            RobotState.notePosition = RobotState.NotePosition.NONE
        })

        controller.x.onTrue(MoveToSide(MechanismSide.AMP)).onFalse(ParallelCommand(Intake.stopCommand(), Hopper.stopCommand()))
        controller.b.onTrue(MoveToSide(MechanismSide.SPEAKER)).onFalse(ParallelCommand(Intake.stopCommand(), Hopper.stopCommand()))

        controller.a.onTrue(InstantCommand { Drivetrain.resetGyro() })

        controller.leftBumper.onTrue(InstantCommand { Shooter.setVolts(0.70, 0.70) }).onFalse(InstantCommand { Shooter.setVolts(0.0, 0.0) })
    }
}
