package org.team9432.lib.drivers.gyro

import org.team9432.lib.util.RotationUtil.toSignedDegrees
import org.team9432.lib.drivers.gyro.GyroIO.GyroIOInputs

class GyroIOSim: GyroIO {
    private var yaw = 0.0
    override fun updateInputs(inputs: LoggedGyroIOInputs) {
        inputs.yaw = toSignedDegrees(yaw)
    }

    override fun setYaw(yaw: Double) {
        this.yaw = yaw
    }
}
