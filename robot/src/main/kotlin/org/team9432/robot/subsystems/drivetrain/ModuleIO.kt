package org.team9432.robot.subsystems.drivetrain

import edu.wpi.first.math.kinematics.SwerveModuleState
import org.team9432.lib.annotation.Logged

interface ModuleIO {
    @Logged
    open class ModuleIOInputs {
        var positionMeters = 0.0
        var speedMetersPerSecond = 0.0
        var angle = 0.0
        var targetAngle = 0.0
        var targetSpeed = 0.0
    }

    fun updateInputs(inputs: ModuleIOInputs)

    fun setBrakeMode(enabled: Boolean) {}
    fun updateIntegratedEncoder() {}
    fun setState(state: SwerveModuleState) {}

    var disabled: Boolean
    val module: Module

    enum class Module(number: Int) {
        FL(1), FR(2), BL(3), BR(4);

        val encoderID = (number * 10) + 0
        val driveID = (number * 10) + 1
        val steerID = (number * 10) + 2
    }
}