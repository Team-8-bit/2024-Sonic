package org.team9432.robot.commands.drivetrain.teleop

import edu.wpi.first.math.controller.ProfiledPIDController
import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Translation2d
import edu.wpi.first.math.kinematics.ChassisSpeeds
import edu.wpi.first.math.trajectory.TrapezoidProfile
import org.littletonrobotics.junction.Logger
import org.team9432.Robot
import org.team9432.Robot.applyFlip
import org.team9432.lib.commandbased.KCommand
import org.team9432.robot.oi.Controls
import org.team9432.robot.sensors.gyro.Gyro
import org.team9432.robot.subsystems.RobotPosition
import org.team9432.robot.subsystems.drivetrain.Drivetrain

class TeleTargetDrive(private val target: () -> Translation2d): KCommand() {
    override val requirements = setOf(Drivetrain)

    private var pid = ProfiledPIDController(0.06, 0.0, 0.0, TrapezoidProfile.Constraints(360.0, 360.0 * 2.0))

    init {
        pid.enableContinuousInput(-180.0, 180.0)
    }

    override fun initialize() {
        pid.reset(Gyro.getYaw().degrees)
    }

    override fun execute() {
        val currentTarget = target.invoke().applyFlip()
        Logger.recordOutput("Drive/AngleTarget", currentTarget)

        val maxSpeedMetersPerSecond = if (Controls.slowDrive) 2.0 else 5.0
        val xSpeed = Controls.xSpeed * maxSpeedMetersPerSecond * Robot.coordinateFlip
        val ySpeed = Controls.ySpeed * maxSpeedMetersPerSecond * Robot.coordinateFlip

        pid.setGoal(RobotPosition.angleTo(currentTarget).degrees)
        val rSpeed = pid.calculate(Gyro.getYaw().degrees)

        val speeds = ChassisSpeeds.fromFieldRelativeSpeeds(xSpeed, ySpeed, rSpeed, Gyro.getYaw())
        Drivetrain.setSpeeds(speeds)
    }
}