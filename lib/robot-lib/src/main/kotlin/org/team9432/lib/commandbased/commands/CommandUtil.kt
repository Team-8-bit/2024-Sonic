package org.team9432.lib.commandbased.commands

import org.team9432.lib.State
import org.team9432.lib.commandbased.KCommand

/** Sets a command to run when the robot is disabled. */
fun KCommand.runsWhenDisabled(value: Boolean): KCommand {
    runsWhenDisabled = value
    return this
}

/** Set the interrupt behavior of a command. */
fun KCommand.withInterruptBehaviour(behavior: KCommand.InterruptionBehavior): KCommand {
    interruptionBehavior = behavior
    return this
}

/** Interrupts a given command after a number of seconds. */
fun KCommand.withTimeout(seconds: Double) = ParallelRaceCommand(WaitCommand(seconds), this)

/** Sets an effect to run after the given delay in simulation only. Useful when waiting for real-robot things like beam breaks. */
fun KCommand.afterSimDelay(seconds: Double, effect: () -> Unit = {}): KCommand {
    return if (State.mode == State.Mode.SIM) {
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