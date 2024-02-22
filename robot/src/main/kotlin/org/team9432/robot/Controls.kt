package org.team9432.robot


import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.lib.commandbased.input.KXboxController
import org.team9432.robot.subsystems.drivetrain.Drivetrain
import org.team9432.robot.subsystems.hood.Hood

object Controls {
    private val controller = KXboxController(0, squareJoysticks = true, joystickDeadband = 0.0)

    init {
        Drivetrain
        Hood

        Drivetrain.defaultCommand = Drivetrain.fieldOrientedDriveCommand({ -controller.leftY }, { -controller.leftX }, { -controller.rightX })

        controller.x.onTrue(Drivetrain.driveToPositionCommand(Pose2d(4.0, 5.0, Rotation2d.fromDegrees(180.0))))
        controller.y.onTrue(Drivetrain.driveToPositionCommand(Pose2d(7.0, 3.0, Rotation2d.fromDegrees(0.0))))

        controller.a.onTrue(InstantCommand { Hood.setAngle(Rotation2d.fromDegrees(30.0)) })
        controller.b.onTrue(InstantCommand { Hood.setAngle(Rotation2d.fromDegrees(0.0)) })

//        controller.a.onTrue(InstantCommand { Drivetrain.resetGyro() })
    }
}
