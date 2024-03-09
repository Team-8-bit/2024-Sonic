package org.team9432.lib.drivers.motors

import com.revrobotics.CANSparkFlex

class KSparkFlex(
    deviceID: Int,
    motorType: MotorType = MotorType.kBrushless,
    inverted: Boolean = false,
    idleMode: IdleMode = IdleMode.kCoast,
    builder: KSparkFlex.() -> Unit = {},
): CANSparkFlex(deviceID, motorType) {
    init {
        setInverted(inverted)
        setIdleMode(idleMode)
        builder()
    }
}