package org.team9432.robot.auto.autos

import org.team9432.lib.commandbased.commands.*
import org.team9432.lib.util.PoseUtil
import org.team9432.robot.auto.AutoBuilder
import org.team9432.robot.auto.commands.AutoShoot
import org.team9432.robot.auto.commands.PullFromSpeakerShooter
import org.team9432.robot.commands.drivetrain.DriveFieldRelativeSpeeds
import org.team9432.robot.subsystems.Hood
import org.team9432.robot.subsystems.Shooter

fun PreloadAndTaxi() = SequentialCommand(
    SuppliedCommand { AutoBuilder.getInitCommand() },
    ParallelDeadlineCommand(
        Hood.Commands.aimAtSpeaker(),
        Shooter.Commands.runAtSpeeds(),
        deadline = SequentialCommand(
            ParallelCommand(
                PullFromSpeakerShooter(),
                WaitCommand(1.0)
            ),
            AutoShoot(),
            WaitCommand(1.0),
            Shooter.Commands.stop(),
            DriveFieldRelativeSpeeds(0.0, 1.0, 0.0).withTimeout(1.0), // Towards Amp Side
            SuppliedCommand {
                DriveFieldRelativeSpeeds(1.0 * PoseUtil.coordinateFlip, 0.0, 0.0).withTimeout(1.0) // Forwards
            }
        )
    )
)