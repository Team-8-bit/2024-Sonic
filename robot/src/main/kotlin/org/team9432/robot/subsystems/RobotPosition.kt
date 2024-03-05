package org.team9432.robot.subsystems

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import org.team9432.lib.wpilib.ChassisSpeeds
import org.team9432.robot.subsystems.drivetrain.Drivetrain
import kotlin.math.atan2
import kotlin.math.hypot

object RobotPosition {
    fun angleTo(pose: Pose2d): Rotation2d {
        val robotPose = Drivetrain.getPose()
        return Rotation2d(atan2(pose.y - robotPose.y, pose.x - robotPose.x))
    }

    fun isNear(pose: Pose2d, epsilon: Double): Boolean {
        val robotPose = Drivetrain.getPose()
        return hypot(robotPose.x - pose.x, robotPose.y - pose.y) < epsilon
    }
}