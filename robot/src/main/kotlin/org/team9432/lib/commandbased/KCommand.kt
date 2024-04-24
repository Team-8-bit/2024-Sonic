package org.team9432.lib.commandbased


/**
 * A state machine representing a complete action to be performed by the robot. Commands are run by
 * the [KCommandScheduler], and can be composed into groups to allow users to build
 * complicated multistep actions without the need to roll the state machine logic themselves.
 *
 * Commands are run synchronously from the main robot loop; no multithreading is used, unless
 * specified explicitly from the command implementation.
 */
abstract class KCommand {
    open fun initialize() {}
    open fun execute() {}
    open fun isFinished(): Boolean = false
    open fun end(interrupted: Boolean) {}

    open val requirements = setOf<KSubsystem>()

    fun schedule() = KCommandScheduler.schedule(this)
    fun cancel() = KCommandScheduler.cancel(this)

    open var runsWhenDisabled: Boolean = false

    var interruptionBehavior = InterruptionBehavior.CANCEL_SELF

    var name: String = this.javaClass.getSimpleName()

    val isScheduled: Boolean
        get() = KCommandScheduler.isScheduled(this)

    var isInGroup = false

    /**
     * An enum describing the command's behavior when another command with a shared requirement is
     * scheduled.
     */
    enum class InterruptionBehavior {
        /**
         * This command ends, [end(true)][.end] is called, and the incoming command is
         * scheduled normally.
         *
         *
         * This is the default behavior.
         */
        CANCEL_SELF,

        /** This command continues, and the incoming command is not scheduled.  */
        CANCEL_INCOMING
    }
}