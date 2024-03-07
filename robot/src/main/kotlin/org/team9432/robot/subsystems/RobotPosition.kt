package org.team9432.robot.subsystems

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.geometry.Transform2d
import edu.wpi.first.math.geometry.Translation2d
import edu.wpi.first.math.interpolation.InterpolatingDoubleTreeMap
import edu.wpi.first.math.interpolation.InterpolatingTreeMap
import edu.wpi.first.math.interpolation.InverseInterpolator
import org.team9432.robot.FieldConstants
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

    fun distanceToSpeaker(): Double {
        val robotPose = Drivetrain.getPose()
        return robotPose.translation.getDistance(FieldConstants.speakerPose.translation)
    }
}