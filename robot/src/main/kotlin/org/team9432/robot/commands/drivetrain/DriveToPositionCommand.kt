package org.team9432.robot.commands.drivetrain

import edu.wpi.first.math.geometry.Pose2d
import org.team9432.lib.commandbased.commands.SimpleCommand
import org.team9432.robot.subsystems.drivetrain.Drivetrain

fun DriveToPositionCommand(
    position: Pose2d,
) = SimpleCommand(
    initialize = {
        Drivetrain.setPositionGoal(position)
        Drivetrain.mode = Drivetrain.DrivetrainMode.PID
    },
    requirements = setOf(Drivetrain),
    isFinished = { Drivetrain.mode != Drivetrain.DrivetrainMode.PID || Drivetrain.atPositionGoal() }
)