package org.team9432.lib.commandbased.commands

import org.team9432.lib.commandbased.KCommand
import org.team9432.lib.commandbased.KCommandScheduler

class RepeatCommand(private val command: KCommand): KCommand() {
    private var ended = false

    init {
        KCommandScheduler.registerComposedCommands(command)
        requirements.addAll(command.requirements)
        name = "Repeat(" + command.name + ")"
    }

    override fun initialize() {
        ended = false
        command.initialize()
    }

    override fun execute() {
        if (ended) {
            ended = false
            command.initialize()
        }
        command.execute()
        if (command.isFinished()) {
            command.end(false)
            ended = true
        }
    }

    override fun isFinished() = false

    override fun end(interrupted: Boolean) {
        command.end(interrupted)
    }
}
