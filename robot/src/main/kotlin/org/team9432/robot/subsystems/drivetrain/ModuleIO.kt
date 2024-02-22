package org.team9432.robot.subsystems.drivetrain

import edu.wpi.first.math.geometry.Rotation2d
import org.team9432.lib.annotation.Logged


interface ModuleIO {
    @Logged
    open class ModuleIOInputs {
        var drivePositionRad = 0.0
        var driveVelocityRadPerSec = 0.0
        var driveAppliedVolts = 0.0
        var driveCurrentAmps = doubleArrayOf()

        var steerAbsolutePosition = Rotation2d()
        var steerPosition = Rotation2d()
        var steerVelocityRadPerSec = 0.0
        var steerAppliedVolts = 0.0
        var steerCurrentAmps = doubleArrayOf()
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
            encoderID = 11,
            driveID = 12,
            steerID = 13,
            driveInverted = false,
            steerInverted = true,
            encoderOffset = Rotation2d.fromDegrees(0.0),
        ),
        FR(
            encoderID = 21,
            driveID = 22,
            steerID = 23,
            driveInverted = true,
            steerInverted = false,
            encoderOffset = Rotation2d.fromDegrees(0.0),
        ),
        BL(
            encoderID = 31,
            driveID = 32,
            steerID = 33,
            driveInverted = false,
            steerInverted = true,
            encoderOffset = Rotation2d.fromDegrees(0.0),
        ),
        BR(
            encoderID = 41,
            driveID = 42,
            steerID = 43,
            driveInverted = true,
            steerInverted = false,
            encoderOffset = Rotation2d.fromDegrees(0.0),
        );
    }
}