package org.team9432.lib.commandbased.commands

import org.team9432.lib.commandbased.KCommand
import org.team9432.lib.commandbased.KSubsystem
import java.util.*

/** A composition that runs a set of commands in parallel, ending when any one of the commands ends and interrupting all the others. */
class ParallelRaceCommand(vararg commands: KCommand): KCommand() {
    private val commands: MutableList<KCommand> = mutableListOf()
    private var finished = true

    override val requirements = mutableSetOf<KSubsystem>()

    init {
        commands.forEach { it.isInGroup = true }

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
