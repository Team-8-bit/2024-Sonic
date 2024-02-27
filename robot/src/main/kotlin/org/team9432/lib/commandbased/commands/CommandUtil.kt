package org.team9432.lib.commandbased.commands

import org.team9432.Robot
import org.team9432.lib.commandbased.KCommand

fun KCommand.runsWhenDisabled(value: Boolean): KCommand {
    runsWhenDisabled = value
    return this
}

fun KCommand.withTimeout(seconds: Double) = ParallelRaceCommand(WaitCommand(seconds), this)

fun KCommand.orSimTimeout(seconds: Double, effect: () -> Unit = {}): KCommand {
    return if (Robot.mode == Robot.Mode.SIM) {
        SequentialCommand(ParallelRaceCommand(WaitCommand(seconds), this), InstantCommand(runnable = effect))
    } else {
        this
    }
}

fun KCommand.afterSimDelay(seconds: Double, effect: () -> Unit = {}): KCommand {
    return if (Robot.mode == Robot.Mode.SIM) {
        ParallelDeadlineCommand(
            SequentialCommand(
                WaitCommand(seconds), InstantCommand(runnable = effect)
            ),
            deadline = this
        )
    } else {
        this
    }
}