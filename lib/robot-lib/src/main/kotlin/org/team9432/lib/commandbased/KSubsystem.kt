package org.team9432.lib.commandbased

/**
 * A robot subsystem. Subsystems are the basic unit of robot organization in the Command-based
 * framework; they encapsulate low-level hardware objects (motor controllers, sensors, etc.) and
 * provide methods through which they can be used by [KCommand]s. Subsystems are used by the
 * [KCommandScheduler]'s resource management system to ensure multiple robot actions are not
 * "fighting" over the same hardware; Commands that use a subsystem should include that subsystem in
 * their [KCommand.requirements], and resources used within a subsystem should
 * generally remain encapsulated and not be shared by other parts of the robot.
 */
abstract class KSubsystem {
    init {
        KCommandScheduler.registerSubsystem(this)
    }

    open fun periodic() {}

    var defaultCommand: KCommand?
        get() = KCommandScheduler.getDefaultCommand(this)
        set(command) = KCommandScheduler.setDefaultCommand(this, command!!)
}
