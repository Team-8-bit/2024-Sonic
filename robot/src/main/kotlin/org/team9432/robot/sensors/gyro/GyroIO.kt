package org.team9432.robot.sensors.gyro

import edu.wpi.first.math.geometry.Rotation2d
import org.team9432.lib.annotation.Logged

interface GyroIO {
    @Logged
    open class GyroIOInputs {
        var connected = false
        var yaw = Rotation2d()
        var yawVelocityDegPerSec = 0.0
    }

    fun updateInputs(inputs: LoggedGyroIOInputs) {}
    fun setYaw(yaw: Double) {}
}