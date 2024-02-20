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

    enum class Module(
       val encoderID: Int,
       val driveID: Int,
       val steerID: Int,
       val driveInverted: Boolean,
       val steerInverted: Boolean,
    ) {
        FL(
            encoderID = 11,
            driveID = 12,
            steerID = 13,
            driveInverted = false,
            steerInverted = true,
        ),
        FR(
            encoderID = 21,
            driveID = 22,
            steerID = 23,
            driveInverted = true,
            steerInverted = false,
        ),
        BL(
            encoderID = 31,
            driveID = 32,
            steerID = 33,
            driveInverted = false,
            steerInverted = true,
        ),
        BR(
            encoderID = 41,
            driveID = 42,
            steerID = 43,
            driveInverted = true,
            steerInverted = false,
        );
    }
}