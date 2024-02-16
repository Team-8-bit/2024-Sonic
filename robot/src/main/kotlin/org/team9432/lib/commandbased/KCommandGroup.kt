package org.team9432.lib.commandbased

abstract class KCommandGroup: KCommand() {
    open fun addCommands(vararg commands: KCommand) {}
}