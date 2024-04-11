package org.team9432.robot

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.geometry.Translation2d
import org.team9432.lib.ifBlueElse
import org.team9432.lib.unit.compareTo
import org.team9432.lib.unit.inMeters
import org.team9432.lib.util.PoseUtil.applyFlip
import org.team9432.robot.subsystems.drivetrain.Drivetrain
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.hypot


object RobotPosition {
    fun angleTo(pose: Translation2d, currentPose: Translation2d = Drivetrain.getPose().translation): Rotation2d {
        return Rotation2d(atan2(pose.y - currentPose.y, pose.x - currentPose.x))
    }

    fun isNear(pose: Pose2d, epsilon: Double): Boolean {
        val robotPose = Drivetrain.getPose()
        return hypot(robotPose.x - pose.x, robotPose.y - pose.y) < epsilon
    }

    fun distanceTo(pose: Translation2d): Double {
        val robotPose = Drivetrain.getPose()
        return robotPose.translation.getDistance(pose)
    }

    fun distanceToSpeaker(): Double {
        return distanceTo(FieldConstants.speakerAimPose.applyFlip())
    }

    fun getSpeakerSide(): SpeakerSide {
        val currentY = Drivetrain.getPose().y
        return when {
            currentY.isCloseTo(FieldConstants.speakerYAxis.inMeters, 0.5) -> SpeakerSide.CENTER
            currentY < FieldConstants.speakerYAxis -> SpeakerSide.LEFT ifBlueElse SpeakerSide.RIGHT
            currentY > FieldConstants.speakerYAxis -> SpeakerSide.RIGHT ifBlueElse SpeakerSide.LEFT
            else -> SpeakerSide.CENTER
        }
    }

    enum class SpeakerSide {
        LEFT, RIGHT, CENTER
    }

    fun Double.isCloseTo(other: Double, range: Double) = abs(this - other) < range
}
