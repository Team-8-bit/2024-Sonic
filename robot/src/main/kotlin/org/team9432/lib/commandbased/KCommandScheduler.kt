package org.team9432.lib.commandbased

import edu.wpi.first.hal.FRCNetComm.tInstances
import edu.wpi.first.hal.FRCNetComm.tResourceType
import edu.wpi.first.hal.HAL
import edu.wpi.first.wpilibj.*
import edu.wpi.first.wpilibj.event.EventLoop
import org.team9432.lib.commandbased.KCommand.InterruptionBehavior
import org.littletonrobotics.junction.LoggedRobot
import org.team9432.lib.commandbased.KSubsystem.SubsystemMode
import java.util.*

object KCommandScheduler {
    private val composedCommands = Collections.newSetFromMap(WeakHashMap<KCommand?, Boolean>())

    // A set of the currently-running commands.
    private val scheduledCommands: MutableSet<KCommand> = LinkedHashSet()

    // A map from required subsystems to their requiring commands. Also used as a set of the
    // currently-required subsystems.
    private val requirements: MutableMap<KSubsystem?, KCommand> = LinkedHashMap()

    // A map from subsystems registered with the scheduler to their default commands.  Also used
    // as a list of currently-registered subsystems.
    private val subsystems: MutableMap<KSubsystem, KCommand?> = LinkedHashMap()

    /**
     * Get the default button poll.
     *
     * @return a reference to the default [EventLoop] object polling buttons.
     */
    val buttonLoop = EventLoop()

    private var isDisabled = false

    // Flag and queues for avoiding ConcurrentModificationException if commands are
    // scheduled/canceled during run
    private var inRunLoop = false
    private val commandsToSchedule: MutableSet<KCommand> = LinkedHashSet()
    private val commandsToCancel: MutableList<KCommand> = ArrayList()
    private val watchdog = Watchdog(LoggedRobot.defaultPeriodSecs) {}

    init {
        HAL.report(tResourceType.kResourceType_Command, tInstances.kCommand2_Scheduler)
    }

    /**
     * Changes the period of the loop overrun watchdog. This should be kept in sync with the
     * TimedRobot period.
     *
     * @param period Period in seconds.
     */
    fun setPeriod(period: Double) {
        watchdog.setTimeout(period)
    }

    /**
     * Initializes a given command, adds its requirements to the list, and performs the init actions.
     *
     * @param command The command to initialize
     * @param requirements The command requirements
     */
    private fun initCommand(command: KCommand, requirements: Set<KSubsystem>) {
        scheduledCommands.add(command)
        for (requirement in requirements) {
            this.requirements[requirement] = command
        }
        command.initialize()
        watchdog.addEpoch(command.name + ".initialize()")
    }

    /**
     * Schedules a command for execution. Does nothing if the command is already scheduled. If a
     * command's requirements are not available, it will only be started if all the commands currently
     * using those requirements have been scheduled as interruptible. If this is the case, they will
     * be interrupted and the command will be scheduled.
     *
     * @param command the command to schedule. If null, no-op.
     */
    private fun schedule(command: KCommand) {
        if (inRunLoop) {
            commandsToSchedule.add(command)
            return
        }
        requireNotComposed(command)

        // Do nothing if the scheduler is disabled, the robot is disabled and the command doesn't run when disabled, or the command is already scheduled.
        if (isDisabled || isScheduled(command) || (RobotState.isDisabled() && !command.runsWhenDisabled)) {
            return
        }

        val requirements = command.requirements

        // Schedule the command if the requirements are not currently in-use.
        if (Collections.disjoint(this.requirements.keys, requirements)) {
            initCommand(command, requirements)
        } else {
            // Else check if the requirements that are in use have all have interruptible commands, and if so, interrupt those commands and schedule the new command.
            for (requirement in requirements) {
                // Get the command that is using the subsystem currently
                val requiring = requiring(requirement)
                // If it can't be interrupted, return
                if (requiring != null && requiring.interruptionBehavior == InterruptionBehavior.CANCEL_INCOMING) {
                    return
                }
            }
            // If all the previous commands were interruptable, cancel them
            for (requirement in requirements) {
                requiring(requirement)?.cancel()
            }
            initCommand(command, requirements)
        }
    }

    /**
     * Schedules multiple commands for execution. Does nothing for commands already scheduled.
     *
     * @param commands the commands to schedule. No-op on null.
     */
    fun schedule(vararg commands: KCommand) {
        commands.forEach { schedule(it) }
    }

    /**
     * Runs a single iteration of the scheduler. The execution occurs in the following order:
     *
     * Subsystem periodic methods are called.
     *
     * Button bindings are polled, and new commands are scheduled from them.
     *
     * Currently-scheduled commands are executed.
     *
     * End conditions get checked on currently-scheduled commands, and commands that are finished have their end methods called and are removed.
     *
     * Any subsystems not being used as requirements have their default methods started.
     */
    fun run() {
        if (isDisabled) return
        watchdog.reset()

        // Run the periodic method of all registered subsystems.
        for (subsystem in subsystems.keys) {
            when (subsystem.mode) {
                SubsystemMode.MANUAL -> subsystem.manualPeriodic()
                SubsystemMode.PID -> subsystem.PIDPeriodic()
                SubsystemMode.DISABLED -> subsystem.disabledPeriodic()
            }
            subsystem.constantPeriodic()
            if (RobotBase.isSimulation()) {
                subsystem.simulationPeriodic()
            }
            watchdog.addEpoch(subsystem.javaClass.getSimpleName() + ".periodic()")
        }

        // Poll buttons for new commands to add.
        buttonLoop.poll()
        watchdog.addEpoch("buttons.run()")

        inRunLoop = true
        // Run scheduled commands, remove finished commands.
        val iterator = scheduledCommands.iterator()
        while (iterator.hasNext()) {
            val command = iterator.next()

            // If the robot is disabled and the command doesn't run while disabled, stop the command
            if (!command.runsWhenDisabled && RobotState.isDisabled()) {
                command.end(true)
                requirements.keys.removeAll(command.requirements)
                iterator.remove()
                watchdog.addEpoch(command.name + ".end(true)")
                continue
            }

            command.execute()
            watchdog.addEpoch(command.name + ".execute()")

            // If the command is finished, stop it
            if (command.isFinished()) {
                command.end(false)
                iterator.remove()
                requirements.keys.removeAll(command.requirements)
                watchdog.addEpoch(command.name + ".end(false)")
            }
        }
        inRunLoop = false

        // Schedule/cancel commands from queues populated during loop
        commandsToSchedule.forEach { schedule(it) }
        commandsToCancel.forEach { cancel(it) }

        commandsToSchedule.clear()
        commandsToCancel.clear()

        // Add default commands for un-required registered subsystems.
        for (subsystemCommand in subsystems.entries) {
            if (!requirements.containsKey(subsystemCommand.key) && subsystemCommand.value != null) {
                schedule(subsystemCommand.value!!)
            }
        }

        watchdog.disable()
        if (watchdog.isExpired()) {
            println("CommandScheduler loop overrun")
            watchdog.printEpochs()
        }
    }

    /**
     * Registers subsystems with the scheduler. This must be called for the subsystem's periodic block
     * to run when the scheduler is run, and for the subsystem's default command to be scheduled. It
     * is recommended to call this from the constructor of your subsystem implementations.
     *
     * @param subsystems the subsystem to register
     */
    fun registerSubsystem(vararg subsystems: KSubsystem) {
        for (subsystem in subsystems) {
            if (this.subsystems.containsKey(subsystem)) {
                DriverStation.reportWarning("Tried to register an already-registered subsystem", true)
                continue
            }
            this.subsystems[subsystem] = null
        }
    }

    /**
     * Un-registers subsystems with the scheduler. The subsystem will no longer have its periodic
     * block called, and will not have its default command scheduled.
     *
     * @param subsystems the subsystem to un-register
     */
    fun unregisterSubsystem(vararg subsystems: KSubsystem?) {
        this.subsystems.keys.removeAll(subsystems.toSet())
    }

    /**
     * Sets the default command for a subsystem. Registers that subsystem if it is not already
     * registered. Default commands will run whenever there is no other command currently scheduled
     * that requires the subsystem. Default commands should be written to never end (i.e. their [ ][KCommand.isFinished] method should return false), as they would simply be re-scheduled if they
     * do. Default commands must also require their subsystem.
     *
     * @param subsystem the subsystem whose default command will be set
     * @param defaultCommand the default command to associate with the subsystem
     */
    fun setDefaultCommand(subsystem: KSubsystem, defaultCommand: KCommand) {
        requireNotComposed(defaultCommand)
        if (!defaultCommand.requirements.contains(subsystem)) {
            throw IllegalArgumentException("Default commands must require their subsystem!")
        }

        if (defaultCommand.interruptionBehavior == InterruptionBehavior.CANCEL_INCOMING) {
            DriverStation.reportWarning("Registering a non-interruptible default command! This will likely prevent any other commands from requiring this subsystem.", true)
        }
        subsystems[subsystem] = defaultCommand
    }

    /**
     * Removes the default command for a subsystem. The current default command will run until another
     * command is scheduled that requires the subsystem, at which point the current default command
     * will not be re-scheduled.
     *
     * @param subsystem the subsystem whose default command will be removed
     */
    fun removeDefaultCommand(subsystem: KSubsystem) {
        subsystems[subsystem] = null
    }

    /**
     * Gets the default command associated with this subsystem. Null if this subsystem has no default
     * command associated with it.
     *
     * @param subsystem the subsystem to inquire about
     * @return the default command associated with the subsystem
     */
    fun getDefaultCommand(subsystem: KSubsystem): KCommand? {
        return subsystems[subsystem]
    }

    /**
     * Cancels commands. The scheduler will only call [KCommand.end] method of the
     * canceled command with `true`, indicating they were canceled (as opposed to finishing
     * normally).
     *
     * Commands will be canceled regardless of [interruption behavior][InterruptionBehavior].
     *
     * @param commands the commands to cancel
     */
    fun cancel(vararg commands: KCommand) {
        if (inRunLoop) {
            commandsToCancel.addAll(commands.toList())
            return
        }
        for (command in commands) {
            if (!isScheduled(command)) continue

            scheduledCommands.remove(command)
            requirements.keys.removeAll(command.requirements)
            command.end(true)
            watchdog.addEpoch(command.name + ".end(true)")
        }
    }

    /** Cancels all commands that are currently scheduled.  */
    fun cancelAll() {
        // Copy to array to avoid concurrent modification.
        cancel(*scheduledCommands.toTypedArray<KCommand>())
    }

    /**
     * Whether the given commands are running. Note that this only works on commands that are directly
     * scheduled by the scheduler; it will not work on commands inside compositions, as the scheduler
     * does not see them.
     *
     * @param commands the command to query
     * @return whether the command is currently scheduled
     */
    fun isScheduled(vararg commands: KCommand): Boolean {
        return scheduledCommands.containsAll(commands.toSet())
    }

    /**
     * Returns the command currently requiring a given subsystem. Null if no command is currently
     * requiring the subsystem
     *
     * @param subsystem the subsystem to be inquired about
     * @return the command currently requiring the subsystem, or null if no command is currently
     * scheduled
     */
    fun requiring(subsystem: KSubsystem): KCommand? {
        return requirements[subsystem]
    }

    /** Disables the command scheduler.  */
    fun disable() {
        isDisabled = true
    }

    /** Enables the command scheduler.  */
    fun enable() {
        isDisabled = false
    }

    /**
     * Register commands as composed. An exception will be thrown if these commands are scheduled
     * directly or added to a composition.
     *
     * @param commands the commands to register
     * @throws IllegalArgumentException if the given commands have already been composed.
     */
    fun registerComposedCommands(vararg commands: KCommand) {
        val commandSet = commands.toSet()
        requireNotComposed(commandSet)
        composedCommands.addAll(commandSet)
    }

    /**
     * Clears the list of composed commands, allowing all commands to be freely used again.
     *
     *
     * WARNING: Using this haphazardly can result in unexpected/undesirable behavior. Do not use
     * this unless you fully understand what you are doing.
     */
    fun clearComposedCommands() {
        composedCommands.clear()
    }

    /**
     * Removes a single command from the list of composed commands, allowing it to be freely used
     * again.
     *
     *
     * WARNING: Using this haphazardly can result in unexpected/undesirable behavior. Do not use
     * this unless you fully understand what you are doing.
     *
     * @param command the command to remove from the list of grouped commands
     */
    fun removeComposedCommand(command: KCommand) {
        composedCommands.remove(command)
    }

    /**
     * Requires that the specified command hasn't been already added to a composition.
     *
     * @param command The command to check
     * @throws IllegalArgumentException if the given commands have already been composed.
     */
    fun requireNotComposed(command: KCommand) {
        if (composedCommands.contains(command)) {
            throw IllegalArgumentException("Commands that have been composed may not be added to another composition or scheduled individually!")
        }
    }

    /**
     * Requires that the specified commands not have been already added to a composition.
     *
     * @param commands The commands to check
     * @throws IllegalArgumentException if the given commands have already been composed.
     */
    fun requireNotComposed(commands: Collection<KCommand>) {
        if (!Collections.disjoint(commands, composedCommands)) {
            throw IllegalArgumentException("Commands that have been composed may not be added to another composition or scheduled individually!")
        }
    }

    /**
     * Check if the given command has been composed.
     *
     * @param command The command to check
     * @return true if composed
     */
    fun isComposed(command: KCommand?): Boolean {
        return composedCommands.contains(command)
    }
}
