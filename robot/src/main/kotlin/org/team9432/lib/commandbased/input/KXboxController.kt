package org.team9432.lib.commandbased.input

import edu.wpi.first.hal.FRCNetComm.tResourceType
import edu.wpi.first.hal.HAL
import edu.wpi.first.wpilibj.GenericHID
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.withSign

/** XboxController with Trigger factories for easier command binding. */
class KXboxController(
    port: Int,
    private val joystickDeadband: Double = 0.2,
    private val squareJoysticks: Boolean = true,
    private val triggerButtonDistance: Double = 0.2,
): GenericHID(port) {
    enum class Button(val value: Int) {
        LEFT_BUMPER(5), RIGHT_BUMPER(6), LEFT_STICK(9), RIGHT_STICK(10), A(1), B(2), X(3), Y(4), BACK(7), START(8),
    }

    enum class Axis(val value: Int) {
        LEFT_X(0), RIGHT_X(4), LEFT_Y(1), RIGHT_Y(5), LEFT_TRIGGER(2), RIGHT_TRIGGER(3),
    }

    init {
        HAL.report(tResourceType.kResourceType_XboxController, port + 1)
    }

    val leftX get() = getRawAxis(Axis.LEFT_X.value).applyDeadband().applySquare()
    val leftY get() = getRawAxis(Axis.LEFT_Y.value).applyDeadband().applySquare()
    val rightX get() = getRawAxis(Axis.RIGHT_X.value).applyDeadband().applySquare()
    val rightY get() = getRawAxis(Axis.RIGHT_Y.value).applyDeadband().applySquare()
    val leftTriggerAxis get() = getRawAxis(Axis.LEFT_TRIGGER.value)
    val rightTriggerAxis get() = getRawAxis(Axis.RIGHT_TRIGGER.value)

    val leftXRaw get() = getRawAxis(Axis.LEFT_X.value)
    val leftYRaw get() = getRawAxis(Axis.LEFT_Y.value)
    val rightXRaw get() = getRawAxis(Axis.RIGHT_X.value)
    val rightYRaw get() = getRawAxis(Axis.RIGHT_Y.value)
    val leftTriggerAxisRaw get() = getRawAxis(Axis.LEFT_TRIGGER.value)
    val rightTriggerAxisRaw get() = getRawAxis(Axis.RIGHT_TRIGGER.value)

    val leftBumper get() = KTrigger { getRawButton(Button.LEFT_BUMPER.value) }
    val rightBumper get() = KTrigger { getRawButton(Button.RIGHT_BUMPER.value) }
    val leftStick get() = KTrigger { getRawButton(Button.LEFT_STICK.value) }
    val rightStick get() = KTrigger { getRawButton(Button.RIGHT_STICK.value) }
    val leftTrigger get() = KTrigger { leftTriggerAxisRaw > triggerButtonDistance }
    val rightTrigger get() = KTrigger { rightTriggerAxisRaw > triggerButtonDistance }
    val a get() = KTrigger { getRawButton(Button.A.value) }
    val b get() = KTrigger { getRawButton(Button.B.value) }
    val x get() = KTrigger { getRawButton(Button.X.value) }
    val y get() = KTrigger { getRawButton(Button.Y.value) }
    val back get() = KTrigger { getRawButton(Button.BACK.value) }
    val start get() = KTrigger { getRawButton(Button.START.value) }


    private fun Double.applyDeadband() = if (abs(this) > joystickDeadband) this else 0.0
    private fun Double.applySquare() = if (squareJoysticks) this.pow(2).withSign(this) else this
}
