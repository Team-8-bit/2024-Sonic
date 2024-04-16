package org.team9432.lib.commandbased.commands

import org.team9432.lib.commandbased.KCommand
import org.team9432.lib.commandbased.KSubsystem
import java.util.*

/** A command composition that runs a set of commands in parallel, ending only when a specific command (the "deadline") ends, interrupting all other commands that are still running at that point. */
class ParallelDeadlineCommand(vararg commands: KCommand, private val deadline: KCommand): KCommand() {
    // maps commands in this composition to whether they are still running
    private val commands: MutableMap<KCommand, Boolean> = HashMap()
    private var finished = true

    override val requirements = mutableSetOf<KSubsystem>()

    init {
        val allCommands = mutableListOf(*commands)
        if (!this.commands.containsKey(deadline)) allCommands.add(deadline)
        allCommands.forEach { it.isInGroup = true }

        for (command in allCommands) {
            require(Collections.disjoint(command.requirements, requirements)) { "Multiple commands in a parallel composition cannot require the same subsystems" }
            this.commands[command] = false
            requirements.addAll(command.requirements)
            runsWhenDisabled = runsWhenDisabled && command.runsWhenDisabled
            if (command.interruptionBehavior == InterruptionBehavior.CANCEL_SELF) {
                interruptionBehavior = InterruptionBehavior.CANCEL_SELF
            }
        }
    }

    override fun initialize() {
        for (commandRunning in commands.entries) {
            commandRunning.key.initialize()
            commandRunning.setValue(true)
        }
        finished = false
    }

    override fun execute() {
        for (commandRunning in commands.entries) {
            if (commandRunning.value == false) continue

            commandRunning.key.execute()
            if (commandRunning.key.isFinished()) {
                commandRunning.key.end(false)
                commandRunning.setValue(false)
                if (commandRunning.key == deadline) {
                    finished = true
                }
            }
        }
    }

    override fun end(interrupted: Boolean) {
        for (entry in commands) {
            if (entry.value) {
                entry.key.end(true)
            }
        }
    }

    override fun isFinished(): Boolean {
        return finished
    }
}
