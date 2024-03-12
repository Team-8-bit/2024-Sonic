package org.team9432.robot.auto.commands

import org.team9432.lib.commandbased.commands.SequentialCommand
import org.team9432.robot.subsystems.shooter.CommandShooter

fun CollectPreloadAndStartShooter() = SequentialCommand(
    PullFromSpeakerShooter(),
    CommandShooter.setSpeed(4000.0, 6000.0)
)