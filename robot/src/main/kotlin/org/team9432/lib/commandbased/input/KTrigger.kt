package org.team9432.lib.commandbased.input

import edu.wpi.first.math.filter.Debouncer
import edu.wpi.first.math.filter.Debouncer.DebounceType
import edu.wpi.first.wpilibj.event.EventLoop
import org.team9432.lib.commandbased.KCommand
import org.team9432.lib.commandbased.KCommandScheduler
import java.util.function.BooleanSupplier

/**
 * This class provides an easy way to link commands to conditions.
 *
 *
 * It is very easy to link a button to a command. For instance, you could link the trigger button
 * of a joystick to a "score" command.
 *
 *
 * Triggers can easily be composed for advanced functionality using the [.and], [.or], [.negate] operators.
 *
 *
 * This class is provided by the NewCommands VendorDep
 */
class KTrigger(private val loop: EventLoop = KCommandScheduler.buttonLoop, private val condition: BooleanSupplier): BooleanSupplier {
    /**
     * Starts the given command whenever the condition changes from `false` to `true`.
     *
     * @param command the command to start
     * @return this trigger, so calls can be chained
     */
    fun onTrue(command: KCommand): KTrigger {
        loop.bind(
            object: Runnable {
                private var pressedLast = condition.asBoolean
                override fun run() {
                    val pressed = condition.asBoolean
                    if (!pressedLast && pressed) {
                        command.schedule()
                    }
                    pressedLast = pressed
                }
            })
        return this
    }

    /**
     * Starts the given command whenever the condition changes from `true` to `false`.
     *
     * @param command the command to start
     * @return this trigger, so calls can be chained
     */
    fun onFalse(command: KCommand): KTrigger {
        loop.bind(
            object: Runnable {
                private var pressedLast = condition.asBoolean
                override fun run() {
                    val pressed = condition.asBoolean
                    if (pressedLast && !pressed) {
                        command.schedule()
                    }
                    pressedLast = pressed
                }
            })
        return this
    }

    /**
     * Starts the given command when the condition changes to `true` and cancels it when the condition
     * changes to `false`.
     *
     *
     * Doesn't re-start the command if it ends while the condition is still `true`. If the command
     * should restart, see [edu.wpi.first.wpilibj2.command.RepeatCommand].
     *
     * @param command the command to start
     * @return this trigger, so calls can be chained
     */
    fun whileTrue(command: KCommand): KTrigger {
        loop.bind(
            object: Runnable {
                private var pressedLast = condition.asBoolean
                override fun run() {
                    val pressed = condition.asBoolean
                    if (!pressedLast && pressed) {
                        command.schedule()
                    } else if (pressedLast && !pressed) {
                        command.cancel()
                    }
                    pressedLast = pressed
                }
            })
        return this
    }

    /**
     * Starts the given command when the condition changes to `false` and cancels it when the
     * condition changes to `true`.
     *
     *
     * Doesn't re-start the command if it ends while the condition is still `false`. If the command
     * should restart, see [edu.wpi.first.wpilibj2.command.RepeatCommand].
     *
     * @param command the command to start
     * @return this trigger, so calls can be chained
     */
    fun whileFalse(command: KCommand): KTrigger {
        loop.bind(
            object: Runnable {
                private var pressedLast = condition.asBoolean
                override fun run() {
                    val pressed = condition.asBoolean
                    if (pressedLast && !pressed) {
                        command.schedule()
                    } else if (!pressedLast && pressed) {
                        command.cancel()
                    }
                    pressedLast = pressed
                }
            })
        return this
    }

    /**
     * Toggles a command when the condition changes from `false` to `true`.
     *
     * @param command the command to toggle
     * @return this trigger, so calls can be chained
     */
    fun toggleOnTrue(command: KCommand): KTrigger {
        loop.bind(
            object: Runnable {
                private var pressedLast = condition.asBoolean
                override fun run() {
                    val pressed = condition.asBoolean
                    if (!pressedLast && pressed) {
                        if (command.isScheduled) {
                            command.cancel()
                        } else {
                            command.schedule()
                        }
                    }
                    pressedLast = pressed
                }
            })
        return this
    }

    /**
     * Toggles a command when the condition changes from `true` to `false`.
     *
     * @param command the command to toggle
     * @return this trigger, so calls can be chained
     */
    fun toggleOnFalse(command: KCommand): KTrigger {
        loop.bind(
            object: Runnable {
                private var pressedLast = condition.asBoolean
                override fun run() {
                    val pressed = condition.asBoolean
                    if (pressedLast && !pressed) {
                        if (command.isScheduled) {
                            command.cancel()
                        } else {
                            command.schedule()
                        }
                    }
                    pressedLast = pressed
                }
            })
        return this
    }

    override fun getAsBoolean(): Boolean {
        return condition.asBoolean
    }

    /**
     * Composes two triggers with logical AND.
     *
     * @param trigger the condition to compose with
     * @return A trigger which is active when both component triggers are active.
     */
    fun and(trigger: BooleanSupplier): KTrigger {
        return KTrigger { condition.asBoolean && trigger.asBoolean }
    }

    /**
     * Composes two triggers with logical OR.
     *
     * @param trigger the condition to compose with
     * @return A trigger which is active when either component trigger is active.
     */
    fun or(trigger: BooleanSupplier): KTrigger {
        return KTrigger { condition.asBoolean || trigger.asBoolean }
    }

    /**
     * Creates a new trigger that is active when this trigger is inactive, i.e. that acts as the
     * negation of this trigger.
     *
     * @return the negated trigger
     */
    fun negate(): KTrigger {
        return KTrigger { !condition.asBoolean }
    }

    /**
     * Creates a new debounced trigger from this trigger - it will become active when this trigger has
     * been active for longer than the specified period.
     *
     * @param seconds The debounce period.
     * @param type The debounce type.
     * @return The debounced trigger.
     */
    fun debounce(seconds: Double, type: DebounceType = DebounceType.kRising): KTrigger {
        return KTrigger(
            condition = object: BooleanSupplier {
                val debouncer = Debouncer(seconds, type)
                override fun getAsBoolean() = debouncer.calculate(condition.asBoolean)
            })
    }
}
