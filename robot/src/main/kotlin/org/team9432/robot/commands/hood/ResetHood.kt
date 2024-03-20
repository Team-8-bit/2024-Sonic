package org.team9432.robot.commands.hood

import org.team9432.lib.commandbased.commands.SequentialCommand
import org.team9432.lib.commandbased.commands.WaitCommand
import org.team9432.robot.subsystems.hood.CommandHood

fun ResetHood() = SequentialCommand(
    CommandHood.setVoltage(-1.0),
    WaitCommand(1.0),
    CommandHood.resetAngle()
)