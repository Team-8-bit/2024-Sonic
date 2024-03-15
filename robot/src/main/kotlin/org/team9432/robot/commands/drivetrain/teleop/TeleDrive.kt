package org.team9432.robot.commands.drivetrain.teleop

import edu.wpi.first.math.kinematics.ChassisSpeeds
import org.team9432.Robot
import org.team9432.lib.commandbased.KCommand
import org.team9432.robot.Controls
import org.team9432.robot.subsystems.drivetrain.Drivetrain
import org.team9432.robot.subsystems.gyro.Gyro

class TeleDrive: KCommand() {
    override val requirements = setOf(Drivetrain)

    override fun execute() {
        val maxSpeedMetersPerSecond = if (Controls.slowDrive) 1.0 else 5.0
        val xSpeed = Controls.xSpeed * maxSpeedMetersPerSecond * Robot.coordinateFlip
        val ySpeed = Controls.ySpeed * maxSpeedMetersPerSecond * Robot.coordinateFlip

        val rSpeed = Math.toRadians(Controls.angle * 360.0)

        Drivetrain.setSpeeds(ChassisSpeeds.fromFieldRelativeSpeeds(xSpeed, ySpeed, rSpeed, Gyro.getYaw()))
    }
}