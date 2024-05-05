package org.team9432.lib.commandbased.commands

import org.team9432.lib.commandbased.KCommand

/** A command that does nothing but ends after a specified condition */
class WaitUntilCommand(private val condition: () -> Boolean): KCommand() {
    override var runsWhenDisabled = true

    override fun isFinished(): Boolean {
        return condition.invoke()
    }
}