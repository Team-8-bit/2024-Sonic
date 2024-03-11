package org.team9432.robot.auto

import org.team9432.lib.commandbased.commands.ParallelCommand
import org.team9432.robot.subsystems.shooter.CommandShooter

fun ExitAuto() = ParallelCommand(
    CommandShooter.stop()
)