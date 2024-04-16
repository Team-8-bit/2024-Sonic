package org.team9432.lib.commandbased.commands

import org.team9432.lib.commandbased.KCommand
import org.team9432.lib.commandbased.KSubsystem

/** Defers command construction to runtime. Runs the command returned by the supplier when this command is initialized, and ends when it ends. Useful for performing runtime tasks before creating a new command. */
class SuppliedCommand(vararg requirements: KSubsystem, private val supplier: () -> KCommand): KCommand() {
    override val requirements = requirements.toSet()
    var command: KCommand? = null

    override fun initialize() {
        command = supplier.invoke()
        name = "Supplied(" + command!!.name + ")"

        command?.initialize()
    }

    override fun execute() {
        command?.execute()
    }

    override fun isFinished(): Boolean {
        return command?.isFinished() ?: true
    }

    override fun end(interrupted: Boolean) {
        command?.end(interrupted)
        command = null
    }
}
