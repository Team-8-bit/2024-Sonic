// https://github.com/Mechanical-Advantage/AdvantageKit/blob/main/example_projects/swerve_drive/src/main/java/frc/robot/subsystems/drive/GyroIOPigeon2.java

package org.team9432.lib.drivers.gyro

import com.ctre.phoenix6.BaseStatusSignal
import com.ctre.phoenix6.StatusCode
import com.ctre.phoenix6.configs.Pigeon2Configuration
import com.ctre.phoenix6.hardware.Pigeon2
import edu.wpi.first.math.geometry.Rotation2d
import org.team9432.robot.Devices


class GyroIOPigeon2: GyroIO {
    private val pigeon = Pigeon2(Devices.PIGEON_ID)
    private val yaw = pigeon.yaw
    private val yawVelocity = pigeon.angularVelocityZWorld

    init {
        pigeon.configurator.apply(Pigeon2Configuration())
        pigeon.configurator.setYaw(0.0)
        yaw.setUpdateFrequency(100.0)
        yawVelocity.setUpdateFrequency(100.0)
        pigeon.optimizeBusUtilization()
    }

    override fun updateInputs(inputs: LoggedGyroIOInputs) {
        inputs.connected = BaseStatusSignal.refreshAll(yaw, yawVelocity).equals(StatusCode.OK)
        inputs.yaw = Rotation2d.fromDegrees(yaw.valueAsDouble)
        inputs.yawVelocityDegPerSec = yawVelocity.valueAsDouble
    }

    override fun setYaw(yaw: Double) {
        pigeon.setYaw(yaw)
    }
}
