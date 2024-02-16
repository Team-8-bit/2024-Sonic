package org.team9432.lib.drivers.gyro

import com.ctre.phoenix6.hardware.Pigeon2
import org.team9432.lib.util.RotationUtil.toSignedDegrees
import org.team9432.robot.Ports

class GyroIOPigeon2: GyroIO {
    private val pigeon = Pigeon2(Ports.PIGEON)

    override fun updateInputs(inputs: LoggedGyroIOInputs) {
        inputs.yaw = toSignedDegrees(pigeon.yaw.value)
        inputs.pitch = toSignedDegrees(pigeon.pitch.value)
        inputs.roll = toSignedDegrees(pigeon.roll.value)
    }

    override fun setYaw(yaw: Double) {
        pigeon.setYaw(yaw)
    }
}
