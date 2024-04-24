package org.team9432.lib.commandbased

/**
 * A class with a periodically called method. Automatically registers with the [KCommandScheduler] when initialized.
 */
abstract class KPeriodic {
    init {
        KCommandScheduler.registerPeriodic(::periodic)
    }

    open fun periodic() {}
}
