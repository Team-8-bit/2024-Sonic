package org.team9432.lib.commandbased


abstract class KPeriodic {
    init {
        KCommandScheduler.addPeriodic(::periodic)
    }

    open fun periodic() {}
}
