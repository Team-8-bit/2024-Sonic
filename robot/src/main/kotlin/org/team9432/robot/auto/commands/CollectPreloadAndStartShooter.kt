package org.team9432.robot.auto.commands

import org.team9432.lib.commandbased.commands.SequentialCommand
import org.team9432.robot.subsystems.Shooter

fun CollectPreloadAndStartShooter() = SequentialCommand(
    PullFromSpeakerShooter(),
    Shooter.Commands.startRunAtSpeeds()
)