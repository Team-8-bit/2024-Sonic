package org.team9432.lib.commandbased.commands

import org.team9432.lib.commandbased.KCommand
import org.team9432.lib.commandbased.KCommandGroup
import org.team9432.lib.commandbased.KCommandScheduler
import java.util.*

class ParallelCommand(vararg commands: KCommand): KCommandGroup() {
    // maps commands in this composition to whether they are still running
    private val commands: MutableMap<KCommand, Boolean> = HashMap()

    init {
        addCommands(*commands)
    }

    override fun addCommands(vararg commands: KCommand) {
        check(!this.commands.containsValue(true)) { "Commands cannot be added to a composition while it's running" }
        KCommandScheduler.registerComposedCommands(*commands)

        for (command in commands) {
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
    }

    override fun execute() {
        for (commandRunning in commands.entries) {
            if (commandRunning.value == false) continue

            commandRunning.key.execute()
            if (commandRunning.key.isFinished()) {
                commandRunning.key.end(false)
                commandRunning.setValue(false)
            }
        }
    }

    override fun end(interrupted: Boolean) {
        // If the command was interrupted, end all running commands
        if (interrupted) {
            for (entry in commands) {
                if (entry.value) {
                    entry.key.end(true)
                }
            }
        }
    }

    override fun isFinished(): Boolean {
        return !commands.containsValue(true)
    }
}
