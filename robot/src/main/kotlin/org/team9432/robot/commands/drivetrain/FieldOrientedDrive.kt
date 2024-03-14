package org.team9432.robot.commands.drivetrain

import edu.wpi.first.math.kinematics.ChassisSpeeds
import org.team9432.lib.commandbased.KCommand
import org.team9432.robot.Controls
import org.team9432.robot.subsystems.RobotPosition
import org.team9432.robot.subsystems.drivetrain.Drivetrain
import org.team9432.robot.subsystems.gyro.Gyro

class FieldOrientedDrive: KCommand() {
    override val requirements = setOf(Drivetrain)

    override fun execute() {
        val maxSpeedMetersPerSecond = if (Controls.slowDrive) 1.0 else 4.0
        val xSpeed = Controls.xSpeed * maxSpeedMetersPerSecond * Drivetrain.coordinateFlip
        val ySpeed = Controls.ySpeed * maxSpeedMetersPerSecond * Drivetrain.coordinateFlip

        val rSpeed = Math.toRadians(Controls.angle * 360.0)

        Drivetrain.setSpeeds(ChassisSpeeds.fromFieldRelativeSpeeds(xSpeed, ySpeed, rSpeed, Gyro.getYaw()))
    }
}