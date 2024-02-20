package org.team9432.lib.commandbased



abstract class KSubsystem {
    init {
        KCommandScheduler.registerSubsystem(this)
    }

    var mode = SubsystemMode.DISABLED

    /** Is always called, used for mainly for logging */
    open fun constantPeriodic() {}

    /** Is called while the robot is simulated */
    fun simulationPeriodic() {}

    /** Is called while the subsystem is in manual mode */
    open fun manualPeriodic() {}

    /** Is called while the subsystem is in PID mode */
    open fun PIDPeriodic() {}

    /** Is called while the subsystem is disabled */
    open fun disabledPeriodic() {}

    var defaultCommand: KCommand?
        get() = KCommandScheduler.getDefaultCommand(this)
        set(command) = KCommandScheduler.setDefaultCommand(this, command!!)


    enum class SubsystemMode {
        MANUAL, PID, DISABLED
    }
}
