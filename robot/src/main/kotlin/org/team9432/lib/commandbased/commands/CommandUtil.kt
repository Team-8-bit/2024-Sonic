package org.team9432.lib.commandbased.commands

import org.team9432.lib.commandbased.KCommand

fun KCommand.runsWhenDisabled(value: Boolean): KCommand {
    runsWhenDisabled = value
    return this
}

fun KCommand.withTimeout(seconds: Double) = ParallelRaceCommand(WaitCommand(seconds), this)