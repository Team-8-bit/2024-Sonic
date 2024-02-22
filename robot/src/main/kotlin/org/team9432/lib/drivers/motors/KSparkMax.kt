package org.team9432.lib.drivers.motors

import com.revrobotics.CANSparkMax

class KSparkMAX(
    deviceID: Int,
    motorType: MotorType = MotorType.kBrushless,
    inverted: Boolean = false,
    idleMode: IdleMode = IdleMode.kCoast,
    builder: KSparkMAX.() -> Unit = {},
): CANSparkMax(deviceID, motorType) {
    init {
        setInverted(inverted)
        setIdleMode(idleMode)
        builder()
    }

    private var lastSpeed = 0.0

    override fun set(speed: Double) {
        if (speed != lastSpeed) {
            super.set(speed)
            lastSpeed = speed
        }
    }

    fun setPIDConstants(p: Double = 0.0, i: Double = 0.0, d: Double = 0.0) {
        val controller = pidController
        controller.p = p
        controller.i = i
        controller.d = d
    }
}