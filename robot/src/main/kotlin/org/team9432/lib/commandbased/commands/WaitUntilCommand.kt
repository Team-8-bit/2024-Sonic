package org.team9432.lib.commandbased.commands

import org.team9432.lib.commandbased.KCommand

class WaitUntilCommand(private val condition: () -> Boolean): KCommand() {
    override var runsWhenDisabled = true

    override fun isFinished(): Boolean {
        return condition.invoke()
    }
}