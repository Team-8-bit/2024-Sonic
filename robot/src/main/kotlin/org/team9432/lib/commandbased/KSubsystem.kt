package org.team9432.lib.commandbased


abstract class KSubsystem {
    init {
        KCommandScheduler.registerSubsystem(this)
    }

    open fun periodic() {}

    var defaultCommand: KCommand?
        get() = KCommandScheduler.getDefaultCommand(this)
        set(command) = KCommandScheduler.setDefaultCommand(this, command!!)
}
