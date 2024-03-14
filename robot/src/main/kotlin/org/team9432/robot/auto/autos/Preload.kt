package org.team9432.robot.auto.autos

import org.team9432.lib.commandbased.commands.*
import org.team9432.robot.auto.AutoBuilder
import org.team9432.robot.auto.commands.*

fun Preload() = SequentialCommand(
    SuppliedCommand {
        AutoBuilder.getInitCommand()
    },
    CollectPreloadAndStartShooter(),
    WaitCommand(1.0),
    ShootFromHopper(),
    WaitCommand(1.0),
    ExitAuto(),
)