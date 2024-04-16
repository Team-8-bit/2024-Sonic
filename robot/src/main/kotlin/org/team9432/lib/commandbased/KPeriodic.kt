package org.team9432.lib.commandbased


abstract class KPeriodic {
    init {
        KCommandScheduler.registerPeriodic(::periodic)
    }

    open fun periodic() {}
}
