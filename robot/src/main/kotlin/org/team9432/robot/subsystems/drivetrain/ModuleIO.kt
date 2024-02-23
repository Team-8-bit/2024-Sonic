package org.team9432.robot.subsystems.drivetrain

import edu.wpi.first.math.geometry.Rotation2d
import org.team9432.lib.annotation.Logged
import org.team9432.robot.Ports.BACK_LEFT_CANCODER
import org.team9432.robot.Ports.BACK_LEFT_DRIVE
import org.team9432.robot.Ports.BACK_LEFT_STEER
import org.team9432.robot.Ports.BACK_RIGHT_CANCODER
import org.team9432.robot.Ports.BACK_RIGHT_DRIVE
import org.team9432.robot.Ports.BACK_RIGHT_STEER
import org.team9432.robot.Ports.FRONT_LEFT_CANCODER
import org.team9432.robot.Ports.FRONT_LEFT_DRIVE
import org.team9432.robot.Ports.FRONT_LEFT_STEER
import org.team9432.robot.Ports.FRONT_RIGHT_CANCODER
import org.team9432.robot.Ports.FRONT_RIGHT_DRIVE
import org.team9432.robot.Ports.FRONT_RIGHT_STEER


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
            encoderID = FRONT_LEFT_CANCODER,
            driveID = FRONT_LEFT_DRIVE,
            steerID = FRONT_LEFT_STEER,
            driveInverted = false,
            steerInverted = true,
            encoderOffset = Rotation2d.fromDegrees(0.0),
        ),
        FR(
            encoderID = FRONT_RIGHT_CANCODER,
            driveID = FRONT_RIGHT_DRIVE,
            steerID = FRONT_RIGHT_STEER,
            driveInverted = true,
            steerInverted = false,
            encoderOffset = Rotation2d.fromDegrees(0.0),
        ),
        BL(
            encoderID = BACK_LEFT_CANCODER,
            driveID = BACK_LEFT_DRIVE,
            steerID = BACK_LEFT_STEER,
            driveInverted = false,
            steerInverted = true,
            encoderOffset = Rotation2d.fromDegrees(0.0),
        ),
        BR(
            encoderID = BACK_RIGHT_CANCODER,
            driveID = BACK_RIGHT_DRIVE,
            steerID = BACK_RIGHT_STEER,
            driveInverted = true,
            steerInverted = false,
            encoderOffset = Rotation2d.fromDegrees(0.0),
        );
    }
}