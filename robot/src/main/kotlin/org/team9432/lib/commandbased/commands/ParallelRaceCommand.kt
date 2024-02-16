package org.team9432.lib.commandbased.commands

import org.team9432.lib.commandbased.KCommand
import org.team9432.lib.commandbased.KCommandGroup
import org.team9432.lib.commandbased.KCommandScheduler
import java.util.*

class ParallelRaceCommand(vararg commands: KCommand): KCommandGroup() {
    private val commands: MutableList<KCommand> = mutableListOf()
    private var finished = true

    init {
        addCommands(*commands)
    }

    override fun addCommands(vararg commands: KCommand) {
        check(finished) { "Commands cannot be added to a composition while it's running" }
        KCommandScheduler.registerComposedCommands(*commands)

        for (command in commands) {
            require(Collections.disjoint(command.requirements, requirements)) { "Multiple commands in a parallel composition cannot require the same subsystems" }
            this.commands.add(command)
            requirements.addAll(command.requirements)
            runsWhenDisabled = runsWhenDisabled && command.runsWhenDisabled
            if (command.interruptionBehavior == InterruptionBehavior.CANCEL_SELF) {
                interruptionBehavior = InterruptionBehavior.CANCEL_SELF
            }
        }
    }

    override fun initialize() {
        finished = false
        for (command in commands) {
            command.initialize()
        }
    }

    override fun execute() {
        for (command in commands) {
            command.execute()
            if (command.isFinished()) {
                finished = true
            }
        }
    }

    override fun end(interrupted: Boolean) {
        for (command in commands) {
            command.end(!command.isFinished())
        }
    }

    override fun isFinished(): Boolean {
        return finished
    }
}
