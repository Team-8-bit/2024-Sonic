package org.team9432.lib.commandbased.commands

import org.team9432.lib.commandbased.KCommand
import org.team9432.lib.commandbased.KCommandGroup
import org.team9432.lib.commandbased.KCommandScheduler

class SequentialCommand(vararg commands: KCommand): KCommandGroup() {
    private val commands = mutableListOf<KCommand>()
    private var currentCommandIndex = -1

    init {
        addCommands(*commands)
    }

    override fun addCommands(vararg commands: KCommand) {
        check(currentCommandIndex == -1) { "Commands cannot be added to a composition while it's running" }
        KCommandScheduler.registerComposedCommands(*commands)
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
