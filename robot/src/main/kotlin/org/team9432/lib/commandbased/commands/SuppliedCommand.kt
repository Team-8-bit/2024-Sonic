package org.team9432.lib.commandbased.commands

import org.team9432.lib.commandbased.KCommand
import org.team9432.lib.commandbased.KSubsystem

class SuppliedCommand(vararg requirements: KSubsystem, private val supplier: () -> KCommand): KCommand() {
    override val requirements = requirements.toSet()
    var command: KCommand? = null

    override fun initialize() {
        command = supplier.invoke()
        name = "Decided(" + command!!.name + ")"

        command!!.initialize()
    }

    override fun execute() = command!!.execute()

    override fun isFinished() = command!!.isFinished()

    override fun end(interrupted: Boolean) {
        command!!.end(interrupted)
        command = null
    }
}
