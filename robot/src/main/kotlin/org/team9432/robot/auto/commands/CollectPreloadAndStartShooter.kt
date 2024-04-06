package org.team9432.robot.auto.commands

import org.team9432.lib.commandbased.commands.SequentialCommand

fun CollectPreload() = SequentialCommand(
    PullFromSpeakerShooter()
)