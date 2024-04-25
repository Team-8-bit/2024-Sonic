package org.team9432.robot

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation3d
import edu.wpi.first.math.geometry.Transform3d
import edu.wpi.first.math.geometry.Translation2d
import org.team9432.lib.constants.EvergreenFieldConstants
import org.team9432.lib.unit.Pose2d
import org.team9432.lib.unit.Translation2d
import org.team9432.lib.unit.inMeters
import org.team9432.lib.unit.meters

object PositionConstants {
    /** Position the robot aims at when shooting in the speaker. */
    val speakerAimPose = Translation2d(0.35.meters, FieldConstants.speakerYCoordinate)


    /** Apriltags below each trap. */
    val trapTags = listOf(14, 15, 16) // Blue

    /** Positions where the robot can line up to (theoretically) shoot in the trap. */
    val trapAimPoses = trapTags.map { tag -> FieldConstants.aprilTagFieldLayout.getTagPose(tag).get().transformBy(Transform3d(1.0, 0.0, 0.0, Rotation3d(0.0, 0.0, Math.toRadians(180.0)))).toPose2d() }

    /** Gets the nearest trap aim position*/
    fun getTrapAimPosition() = trapAimPoses.minBy { RobotPosition.distanceTo(it.translation).inMeters }


    /** Position the robot aims when feeding, it is in the amp corner of the field. */
    val feedAimPose = Translation2d(0.0.meters, EvergreenFieldConstants.lengthY)

    /** Position the robot should feed notes from. */
    val feedPose = Translation2d(EvergreenFieldConstants.centerX + 1.0.meters, 1.0.meters).angleAtFeedCorner()

    /** Returns a [Pose2d] using this [Translation2d]'s coordinates, rotated to point at the corner where notes should be fed to. */
    private fun Translation2d.angleAtFeedCorner() = Pose2d(x, y, RobotPosition.angleTo(feedAimPose, currentPose = Translation2d(x, y)))
}