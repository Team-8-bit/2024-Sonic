package org.team9432.robot.subsystems

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.geometry.Transform2d
import edu.wpi.first.math.geometry.Translation2d
import edu.wpi.first.math.kinematics.ChassisSpeeds
import org.team9432.robot.FieldConstants
import org.team9432.robot.subsystems.drivetrain.Drivetrain
import org.team9432.robot.subsystems.gyro.Gyro
import kotlin.math.atan2
import kotlin.math.hypot


object RobotPosition {
    fun angleTo(pose: Pose2d, futureTime: Double? = null): Rotation2d {
        val robotPose = futureTime?.let { getFuturePose(futureTime) } ?: Drivetrain.getPose()
        return Rotation2d(atan2(pose.y - robotPose.y, pose.x - robotPose.x))
    }

    fun isNear(pose: Pose2d, epsilon: Double): Boolean {
        val robotPose = Drivetrain.getPose()
        return hypot(robotPose.x - pose.x, robotPose.y - pose.y) < epsilon
    }

    fun distanceTo(pose: Pose2d, futureTime: Double? = null): Double {
        val robotPose = futureTime?.let { getFuturePose(futureTime) } ?: Drivetrain.getPose()
        return robotPose.translation.getDistance(pose.translation)
    }

    fun distanceToSpeaker(futureTime: Double? = null): Double {
        return distanceTo(FieldConstants.speakerPose, futureTime)
    }

    fun getFieldRelativeSpeeds() = ChassisSpeeds.fromFieldRelativeSpeeds(Drivetrain.getSpeeds(), Gyro.getYaw())

    private fun getMovementIn(futureTime: Double): Transform2d {
        val currentSpeeds = Drivetrain.getSpeeds()
        return Transform2d(
            Translation2d(
                currentSpeeds.vxMetersPerSecond * futureTime,
                currentSpeeds.vyMetersPerSecond * futureTime
            ),
            Rotation2d.fromRadians(currentSpeeds.omegaRadiansPerSecond * futureTime)
        )
    }

    fun getFuturePose(futureTime: Double): Pose2d {
        return Drivetrain.getPose().transformBy(getMovementIn(futureTime))
    }
}