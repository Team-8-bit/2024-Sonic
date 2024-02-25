package org.team9432.robot


import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.lib.commandbased.commands.ParallelCommand
import org.team9432.lib.commandbased.commands.SimpleCommand
import org.team9432.lib.commandbased.input.KXboxController
import org.team9432.robot.commands.auto.testAuto
import org.team9432.robot.commands.intakeAndScore
import org.team9432.robot.commands.moveToSide
import org.team9432.robot.subsystems.drivetrain.Drivetrain
import org.team9432.robot.subsystems.hood.Hood
import org.team9432.robot.subsystems.hopper.Hopper
import org.team9432.robot.subsystems.intake.Intake
import org.team9432.robot.subsystems.shooter.Shooter

object Controls {
    private val controller = KXboxController(0, squareJoysticks = true, joystickDeadband = 0.1)

    init {
        Drivetrain
        Hood
        Shooter
        Hopper
        Intake

        Drivetrain.defaultCommand = Drivetrain.fieldOrientedDriveCommand({ -controller.leftY }, { -controller.leftX }, { controller.rightX }, maxSpeedMetersPerSecond = 3.5)
        Hopper.defaultCommand = SimpleCommand(execute = { Hopper.setVoltage(0.0) }, requirements = mutableSetOf(Hopper))
        Intake.defaultCommand = SimpleCommand(execute = { Intake.stopCommand() }, requirements = mutableSetOf(Intake))

        controller.rightBumper.onTrue(Drivetrain.fieldOrientedDriveCommand({ -controller.leftY }, { -controller.leftX }, { controller.rightX }, maxSpeedMetersPerSecond = 6.0))
        controller.rightTrigger.onTrue(intakeAndScore()).onFalse(Intake.stopCommand())

//        controller.b.onTrue(InstantCommand { Intake.runVolts(9.0, 9.0) }).onFalse(InstantCommand { Intake.runVolts(0.0, 0.0) })
//        controller.a.onTrue(InstantCommand { Intake.runVolts(-9.0, -9.0) }).onFalse(InstantCommand { Intake.runVolts(0.0, 0.0) })

        controller.x.onTrue(moveToSide(MechanismSide.AMP)).onFalse(ParallelCommand(Intake.stopCommand(), Hopper.stopCommand()))
        controller.b.onTrue(moveToSide(MechanismSide.SPEAKER)).onFalse(ParallelCommand(Intake.stopCommand(), Hopper.stopCommand()))

//        controller.a.onTrue(InstantCommand { Hopper.setVoltage(5.0) }).onFalse(InstantCommand { Hopper.setVoltage(0.0) })
//        controller.y.onTrue(InstantCommand { Hopper.setVoltage(-5.0) }).onFalse(InstantCommand { Hopper.setVoltage(0.0) })

        controller.a.onTrue(InstantCommand { Drivetrain.resetGyro() })

        controller.leftBumper.onTrue(InstantCommand { Shooter.setVolts(0.70, 0.70) }).onFalse(InstantCommand { Shooter.setVolts(0.0, 0.0) })


//        controller.x.onTrue(Drivetrain.driveToPositionCommand(Pose2d(4.0, 5.0, Rotation2d.fromDegrees(180.0))))
//        controller.y.onTrue(Drivetrain.driveToPositionCommand(Pose2d(7.0, 3.0, Rotation2d.fromDegrees(0.0))))

//        controller.a.onTrue(InstantCommand { Hood.setAngle(Rotation2d.fromDegrees(30.0)) })
//        controller.b.onTrue(InstantCommand { Hood.setAngle(Rotation2d.fromDegrees(0.0)) })

//        controller.a.onTrue(InstantCommand { Shooter.setSpeed(3000.0, 3000.0) })
//        controller.b.onTrue(InstantCommand { Shooter.setSpeed(2000.0, 2000.0) })
//        controller.x.onTrue(InstantCommand { Shooter.setSpeed(1000.0, 1000.0) })
//        controller.y.onTrue(InstantCommand { Shooter.setSpeed(0000.0, 0000.0) })

        //controller.a.onTrue(testAuto)

//        controller.a.onTrue(InstantCommand { Drivetrain.resetGyro() })
    }
}
