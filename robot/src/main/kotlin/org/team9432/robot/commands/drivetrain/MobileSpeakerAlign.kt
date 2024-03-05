package org.team9432.robot.commands.drivetrain

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.kinematics.ChassisSpeeds
import org.littletonrobotics.junction.Logger
import org.team9432.lib.commandbased.KCommand
import org.team9432.robot.Controls
import org.team9432.robot.subsystems.RobotPosition
import org.team9432.robot.subsystems.drivetrain.Drivetrain
import org.team9432.robot.subsystems.gyro.Gyro

class TargetDrive(private val target: () -> Pose2d): KCommand() {
    override val requirements = setOf(Drivetrain)

    override fun execute() {
        val currentTarget = target.invoke()
        Logger.recordOutput("Drive/AngleTarget", currentTarget)


        val maxSpeedMetersPerSecond = if (Controls.fastDrive) 6.0 else 2.5
        val xSpeed = Controls.xSpeed * maxSpeedMetersPerSecond
        val ySpeed = Controls.ySpeed * maxSpeedMetersPerSecond

        Drivetrain.setAngleGoal(RobotPosition.angleTo(currentTarget))
        val rSpeed = Drivetrain.calculateAngleSpeed()

        val speeds = ChassisSpeeds.fromFieldRelativeSpeeds(xSpeed, ySpeed, rSpeed, Gyro.getYaw())
        Drivetrain.setSpeeds(speeds)
    }
}