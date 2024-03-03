package org.team9432.robot.subsystems.drivetrain

import edu.wpi.first.math.geometry.Rotation2d
import org.team9432.lib.annotation.Logged
import org.team9432.robot.Devices.BACK_LEFT_CANCODER_ID
import org.team9432.robot.Devices.BACK_LEFT_DRIVE_ID
import org.team9432.robot.Devices.BACK_LEFT_STEER_ID
import org.team9432.robot.Devices.BACK_RIGHT_CANCODER_ID
import org.team9432.robot.Devices.BACK_RIGHT_DRIVE_ID
import org.team9432.robot.Devices.BACK_RIGHT_STEER_ID
import org.team9432.robot.Devices.FRONT_LEFT_CANCODER_ID
import org.team9432.robot.Devices.FRONT_LEFT_DRIVE_ID
import org.team9432.robot.Devices.FRONT_LEFT_STEER_ID
import org.team9432.robot.Devices.FRONT_RIGHT_CANCODER_ID
import org.team9432.robot.Devices.FRONT_RIGHT_DRIVE_ID
import org.team9432.robot.Devices.FRONT_RIGHT_STEER_ID


interface ModuleIO {
    @Logged
    open class ModuleIOInputs {
        var drivePositionRad = 0.0
        var driveVelocityRadPerSec = 0.0
        var driveAppliedVolts = 0.0
        var driveCurrentAmps = 0.0

        var steerAbsolutePosition = Rotation2d()
        var steerPosition = Rotation2d()
        var steerVelocityRadPerSec = 0.0
        var steerAppliedVolts = 0.0
        var steerCurrentAmps = 0.0
    }

    fun updateInputs(inputs: ModuleIOInputs)

    fun setDriveVoltage(volts: Double) {}
    fun setSteerVoltage(volts: Double) {}

    fun setBrakeMode(enabled: Boolean) {}

    val module: Module

    enum class Module(
        val encoderID: Int,
        val driveID: Int,
        val steerID: Int,
        val driveInverted: Boolean,
        val steerInverted: Boolean,
        val encoderOffset: Rotation2d,
    ) {
        FL(
            encoderID = FRONT_LEFT_CANCODER_ID,
            driveID = FRONT_LEFT_DRIVE_ID,
            steerID = FRONT_LEFT_STEER_ID,
            driveInverted = true,
            steerInverted = false,
            encoderOffset = Rotation2d.fromDegrees(18.193),
        ),
        FR(
            encoderID = FRONT_RIGHT_CANCODER_ID,
            driveID = FRONT_RIGHT_DRIVE_ID,
            steerID = FRONT_RIGHT_STEER_ID,
            driveInverted = false,
            steerInverted = true,
            encoderOffset = Rotation2d.fromDegrees(70.225),
        ),
        BL(
            encoderID = BACK_LEFT_CANCODER_ID,
            driveID = BACK_LEFT_DRIVE_ID,
            steerID = BACK_LEFT_STEER_ID,
            driveInverted = false,
            steerInverted = true,
            encoderOffset = Rotation2d.fromDegrees(-82.090),
        ),
        BR(
            encoderID = BACK_RIGHT_CANCODER_ID,
            driveID = BACK_RIGHT_DRIVE_ID,
            steerID = BACK_RIGHT_STEER_ID,
            driveInverted = true,
            steerInverted = false,
            encoderOffset = Rotation2d.fromDegrees(128.320),
        );
    }
}