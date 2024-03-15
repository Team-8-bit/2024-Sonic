package org.team9432.robot.auto.autos

import org.team9432.lib.commandbased.commands.SequentialCommand
import org.team9432.lib.commandbased.commands.SuppliedCommand
import org.team9432.lib.commandbased.commands.WaitCommand
import org.team9432.robot.auto.AutoBuilder
import org.team9432.robot.auto.commands.AutoShoot
import org.team9432.robot.auto.commands.CollectPreloadAndStartShooter
import org.team9432.robot.auto.commands.ExitAuto

fun Preload() = SequentialCommand(
    SuppliedCommand {
        AutoBuilder.getInitCommand()
    },
    CollectPreloadAndStartShooter(),
    WaitCommand(1.0),
    AutoShoot(),
    WaitCommand(1.0),
    ExitAuto(),
)