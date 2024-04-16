package org.team9432.lib.commandbased.commands

import org.team9432.lib.commandbased.KCommand
import org.team9432.lib.commandbased.KSubsystem

/** A command composition that runs a list of commands in sequence. */
class SequentialCommand(vararg commands: KCommand): KCommand() {
    private val commands = mutableListOf<KCommand>()
    private var currentCommandIndex = -1

    override val requirements = mutableSetOf<KSubsystem>()

    init {
        commands.forEach { it.isInGroup = true }
        for (command in commands) {
            this.commands.add(command)
            requirements.addAll(command.requirements)
            runsWhenDisabled = runsWhenDisabled && command.runsWhenDisabled
            if (command.interruptionBehavior == InterruptionBehavior.CANCEL_SELF) {
                interruptionBehavior = InterruptionBehavior.CANCEL_SELF
            }
        }
    }

    override fun initialize() {
        currentCommandIndex = 0
        if (commands.isNotEmpty()) {
            commands[0].initialize()
        }
    }

    override fun execute() {
        if (commands.isEmpty()) return

        val currentCommand = commands[currentCommandIndex]
        currentCommand.execute()

        // If the current command is finished, add one to the current command index and run the next one (checking to make sure there is one)
        if (currentCommand.isFinished()) {
            currentCommand.end(false)
            currentCommandIndex++
            if (currentCommandIndex < commands.size) {
                commands[currentCommandIndex].initialize()
            }
        }
    }

    override fun end(interrupted: Boolean) {
        if (interrupted && currentCommandIndex > -1) {
            commands.getOrNull(currentCommandIndex)?.end(true)
        }
        currentCommandIndex = -1
    }

    override fun isFinished(): Boolean {
        return currentCommandIndex == commands.size
    }
}
