package org.team9432.lib.commandbased.commands

import org.team9432.lib.commandbased.KCommand
import org.team9432.lib.commandbased.KSubsystem

class SimpleCommand(
    private val initialize: () -> Unit = {},
    private val execute: () -> Unit = {},
    private val isFinished: () -> Boolean = { false },
    private val end: (interrupted: Boolean) -> Unit = {},
    override val requirements: MutableSet<KSubsystem> = mutableSetOf(),
): KCommand() {
    override fun initialize() {
        initialize.invoke()
    }

    override fun execute() {
        execute.invoke()
    }

    override fun isFinished(): Boolean {
        return isFinished.invoke()
    }

    override fun end(interrupted: Boolean) {
        end.invoke(interrupted)
    }
}
