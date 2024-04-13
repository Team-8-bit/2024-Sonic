package org.team9432.robot.auto.autos

import org.team9432.lib.commandbased.commands.*
import org.team9432.robot.auto.AutoBuilder
import org.team9432.robot.auto.commands.AutoShoot
import org.team9432.robot.auto.commands.PullFromSpeakerShooter
import org.team9432.robot.subsystems.Hood
import org.team9432.robot.subsystems.Shooter

fun Preload() = SequentialCommand(
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
        )
    )
)