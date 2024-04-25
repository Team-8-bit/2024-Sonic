package org.team9432.lib.commandbased

import edu.wpi.first.hal.FRCNetComm.tInstances
import edu.wpi.first.hal.FRCNetComm.tResourceType
import edu.wpi.first.hal.HAL
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.RobotState
import edu.wpi.first.wpilibj.Watchdog
import org.littletonrobotics.junction.LOOP_PERIOD
import org.team9432.lib.commandbased.KCommand.InterruptionBehavior
import org.team9432.lib.commandbased.KCommandScheduler.registerPeriodic
import org.team9432.lib.commandbased.KCommandScheduler.registerSubsystem
import org.team9432.lib.commandbased.KCommandScheduler.run
import org.team9432.lib.unit.inSeconds
import java.util.*

/**
 * The scheduler responsible for running [KCommand]s. A Command-based robot should call [run]
 * on the object in its periodic block in order to run commands
 * synchronously from the main loop. Subsystems and other periodic calls should be registered with the scheduler using
 * [registerSubsystem] and [registerPeriodic] respectively in order for their periodic()
 * methods to be called and for their default commands to be scheduled.
 *
 * Based on the wpilib commandscheduler
 */
object KCommandScheduler {
    // A set of the currently-running commands.
    private val scheduledCommands: MutableSet<KCommand> = LinkedHashSet()

    // A map from required subsystems to their requiring commands. Also used as a set of the currently-required subsystems.
    private val requirements: MutableMap<KSubsystem?, KCommand> = LinkedHashMap()

    // A map from subsystems registered with the scheduler to their default commands. Also used as a list of currently-registered subsystems.
    private val subsystems: MutableMap<KSubsystem, KCommand?> = LinkedHashMap()

    // A list of other functions called periodically, including button bindings.
    private val additionalPeriodics: MutableSet<() -> Unit> = mutableSetOf()

    var isDisabled = false

    // Flag and queue for operations that can't happen while the scheduler is running
    private var inRunLoop = false
    private val afterLoopQueue = mutableListOf<() -> Unit>()

    private val watchdog = Watchdog(LOOP_PERIOD.inSeconds) {}

    init {
        HAL.report(tResourceType.kResourceType_Command, tInstances.kCommand2_Scheduler)
    }

    /**
     * Runs a single iteration of the scheduler. The execution occurs in the following order:
     *
     * Subsystem periodic methods are called.
     *
     * Additional periodics (including button bindings) are polled, and new commands are scheduled from them.
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
            subsystem.periodic()

            watchdog.addEpoch(subsystem.javaClass.getSimpleName() + ".periodic()")
        }

        // Call each additional periodic method (this includes adding new commands from buttons)
        additionalPeriodics.forEach { it.invoke() }

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

        // Run through all operations that happened during the loop
        afterLoopQueue.forEach { it.invoke() }
        afterLoopQueue.clear()

        // Add default commands for un-required registered subsystems.
        for (subsystemCommand in subsystems.entries) {
            if (!requirements.containsKey(subsystemCommand.key) && subsystemCommand.value != null) {
                schedule(subsystemCommand.value!!)
            }
        }

        watchdog.disable()
        if (watchdog.isExpired) {
            println("CommandScheduler loop overrun")
            watchdog.printEpochs()
        }
    }

    /**
     * Schedules a command for execution. Does nothing if the command is already scheduled. If a
     * command's requirements are not available, it will only be started if all the commands currently
     * using those requirements have been scheduled as interruptible. If this is the case, they will
     * be interrupted and the command will be scheduled.
     */
    fun schedule(command: KCommand) {
        if (inRunLoop) {
            afterLoopQueue.add { schedule(command) }
            return
        }
        if (command.isInGroup) throw Exception("You can't schedule commands that are already part of a group!")

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

    /** Initializes a given command, adds its requirements to the list, and runs [KCommand.initialize]. */
    private fun initCommand(command: KCommand, requirements: Set<KSubsystem>) {
        scheduledCommands.add(command)
        for (requirement in requirements) {
            this.requirements[requirement] = command
        }
        command.initialize()
        watchdog.addEpoch(command.name + ".initialize()")
    }

    /** Registers the given runnable to be called periodically. */
    fun registerPeriodic(runnable: () -> Unit) {
        if (inRunLoop) {
            afterLoopQueue.add { registerPeriodic(runnable) }
            return
        }
        additionalPeriodics.add(runnable)
    }

    /** Registers the given runnable to be called periodically. */
    fun registerPeriodic(runnable: Runnable) = registerPeriodic(runnable::run)

    /**
     * Registers a subsystem with the scheduler. This must be called for the subsystem's periodic block
     * to run when the scheduler is run, and for the subsystem's default command to be scheduled. It
     * is recommended to call this from the constructor of your subsystem implementations.
     */
    fun registerSubsystem(subsystem: KSubsystem) {
        if (inRunLoop) {
            afterLoopQueue.add { registerSubsystem(subsystem) }
            return
        }
        if (this.subsystems.containsKey(subsystem)) {
            DriverStation.reportWarning("Tried to register an already-registered subsystem", true)
        }
        this.subsystems[subsystem] = null
    }

    /**
     * Sets the default command for a subsystem. Registers that subsystem if it is not already registered.
     * Default commands will run whenever there is no other command currently scheduled that requires the subsystem.
     * Default commands should be written to never end (i.e. their [KCommand.isFinished] method should return false), as they would simply be re-scheduled if they do.
     * Default commands must also require their subsystem.
     */
    fun setDefaultCommand(subsystem: KSubsystem, defaultCommand: KCommand) {
        if (defaultCommand.isInGroup) throw Exception("You can't schedule commands that are already part of a group!")
        if (!defaultCommand.requirements.contains(subsystem)) throw Exception("Default commands must require their subsystem!")

        if (defaultCommand.interruptionBehavior == InterruptionBehavior.CANCEL_INCOMING) {
            DriverStation.reportWarning("Registering a non-interruptible default command! This will likely prevent any other commands from requiring this subsystem.", true)
        }
        subsystems[subsystem] = defaultCommand
    }

    /**
     * Removes the default command for a subsystem. The current default command will run until another
     * command is scheduled that requires the subsystem, at which point the current default command
     * will not be re-scheduled.
     */
    fun removeDefaultCommand(subsystem: KSubsystem) {
        subsystems[subsystem] = null
    }

    /** Gets the default command associated with this subsystem. Null if this subsystem has no default command associated with it. */
    fun getDefaultCommand(subsystem: KSubsystem): KCommand? {
        return subsystems[subsystem]
    }

    /**
     * Cancels a command. The scheduler will call [KCommand.end] method of the canceled command with `true`, indicating they were canceled (as opposed to finishing normally).
     * The command will be canceled regardless of its [InterruptionBehavior].
     */
    fun cancel(command: KCommand) {
        if (inRunLoop) {
            afterLoopQueue.add { cancel(command) }
            return
        }

        if (!isScheduled(command)) return

        scheduledCommands.remove(command)
        requirements.keys.removeAll(command.requirements)
        command.end(true)
        watchdog.addEpoch(command.name + ".end(true)")
    }

    /** Cancels all commands that are currently scheduled.  */
    fun cancelAll() = scheduledCommands.forEach { cancel(it) }

    /**
     * Whether the command is running. Note that this only works on commands that are directly
     * scheduled by the scheduler; it will not work on commands inside compositions, as the scheduler
     * does not see them.
     */
    fun isScheduled(command: KCommand) = scheduledCommands.contains(command)

    /** Returns the command currently requiring a given subsystem. Null if no command is currently requiring the subsystem. */
    fun requiring(subsystem: KSubsystem): KCommand? = requirements[subsystem]
}
