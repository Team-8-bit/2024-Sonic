package org.team9432.robot.commands.drivetrain

import org.team9432.lib.commandbased.commands.SimpleCommand
import org.team9432.lib.wpilib.ChassisSpeeds
import org.team9432.robot.subsystems.drivetrain.Drivetrain

fun FieldOrientedDriveCommand(
    xJoystickInput: () -> Double,
    yJoystickInput: () -> Double,
    angleJoystickInput: () -> Double,
    maxSpeedMetersPerSecond: Double = 5.0,
    maxSpeedDegreesPerSecond: Double = 360.0,
) = SimpleCommand(
    execute = {
        val xSpeed = xJoystickInput.invoke() * maxSpeedMetersPerSecond
        val ySpeed = yJoystickInput.invoke() * maxSpeedMetersPerSecond
        val radiansPerSecond = Math.toRadians(angleJoystickInput.invoke() * maxSpeedDegreesPerSecond)
        Drivetrain.setManualSpeeds(ChassisSpeeds.fromFieldRelativeSpeeds(xSpeed, ySpeed, radiansPerSecond, Drivetrain.yaw))
    },
    end = { Drivetrain.stop() },
    isFinished = { false },
    requirements = setOf(Drivetrain),
    initialize = { Drivetrain.mode = Drivetrain.DrivetrainMode.MANUAL }
)
