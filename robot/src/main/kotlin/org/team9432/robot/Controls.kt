package org.team9432.robot


import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.lib.commandbased.commands.ParallelCommand
import org.team9432.lib.commandbased.commands.SimpleCommand
import org.team9432.lib.commandbased.input.KXboxController
import org.team9432.robot.commands.intakeAndScore
import org.team9432.robot.commands.moveToSide
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

        Drivetrain.defaultCommand = Drivetrain.fieldOrientedDriveCommand({ -controller.leftY }, { -controller.leftX }, { -controller.rightX }, maxSpeedMetersPerSecond = 3.5)
        Hopper.defaultCommand = SimpleCommand(execute = { Hopper.setVoltage(0.0) }, requirements = setOf(Hopper))
        Intake.defaultCommand = SimpleCommand(execute = { Intake.stopCommand() }, requirements = setOf(Intake))

        controller.rightBumper.whileTrue(Drivetrain.fieldOrientedDriveCommand({ -controller.leftY }, { -controller.leftX }, { -controller.rightX }, maxSpeedMetersPerSecond = 6.0))
        controller.rightTrigger.onTrue(intakeAndScore()).onFalse(Intake.stopCommand())

        controller.x.onTrue(moveToSide(MechanismSide.AMP)).onFalse(ParallelCommand(Intake.stopCommand(), Hopper.stopCommand()))
        controller.b.onTrue(moveToSide(MechanismSide.SPEAKER)).onFalse(ParallelCommand(Intake.stopCommand(), Hopper.stopCommand()))

        controller.a.onTrue(InstantCommand { Drivetrain.resetGyro() })

        controller.leftBumper.onTrue(InstantCommand { Shooter.setVolts(0.70, 0.70) }).onFalse(InstantCommand { Shooter.setVolts(0.0, 0.0) })
    }
}
