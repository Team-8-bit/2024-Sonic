package org.team9432.robot

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.geometry.Translation2d
import org.team9432.lib.ifBlueElse
import org.team9432.lib.unit.Length
import org.team9432.lib.unit.compareTo
import org.team9432.lib.unit.inMeters
import org.team9432.lib.unit.meters
import org.team9432.lib.util.PoseUtil.applyFlip
import org.team9432.robot.subsystems.drivetrain.Drivetrain
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.hypot


object RobotPosition {
    /** Returns the angle between the two given positions. */
    fun angleTo(pose: Translation2d, currentPose: Translation2d = Drivetrain.getPose().translation): Rotation2d {
        return Rotation2d(atan2(pose.y - currentPose.y, pose.x - currentPose.x))
    }

    /** Returns true if the robot is within [epsilon] of the given pose. */
    fun isNear(pose: Pose2d, epsilon: Length): Boolean {
        val robotPose = Drivetrain.getPose()
        return hypot(robotPose.x - pose.x, robotPose.y - pose.y) < epsilon.inMeters
    }

    /** Return the distance from the robot to another point. */
    fun distanceTo(pose: Translation2d): Length {
        val robotPose = Drivetrain.getPose()
        return robotPose.translation.getDistance(pose).meters
    }

    /** Return the distance from the robot to the speaker. */
    fun distanceToSpeaker(): Length {
        return distanceTo(FieldConstants.speakerAimPose.applyFlip())
    }

    /** Get which side of the speaker the robot is on as though you are standing behind the driver station. */
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

    /** Returns true if this double is within [range] of [other]. */
    fun Double.isCloseTo(other: Double, range: Double) = abs(this - other) < range
}
