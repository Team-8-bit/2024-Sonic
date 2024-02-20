package org.team9432.lib.drivers.gyro

import org.team9432.lib.annotation.Logged

interface GyroIO {
    @Logged
    open class GyroIOInputs {
        var yaw = 0.0
        var pitch = 0.0
        var roll = 0.0
    }

    fun updateInputs(inputs: LoggedGyroIOInputs) {}
    fun setYaw(yaw: Double) {}
}