package org.team9432.robot

import edu.wpi.first.apriltag.AprilTagFields
import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation3d
import edu.wpi.first.math.geometry.Transform3d
import edu.wpi.first.math.geometry.Translation2d
import org.team9432.lib.unit.*

// All positions are on the blue side of the field and are flipped as needed
// TODO: EvergreenFieldConstants class with things like field size
object FieldConstants {
    val aprilTagFieldLayout = AprilTagFields.k2024Crescendo.loadAprilTagLayoutField()

    val width = 26.0.feet + 11.25.inches
    val height = 54.0.feet + 3.25.inches
    val centerLine = width / 2
    val midLine = height / 2

    private val allianceNoteXCoordinateBlue = 9.0.feet + 6.0.inches
    private val allianceNoteYSpacing = 4.0.feet + 9.0.inches
    private val centerNoteYSpacing = 5.0.feet + 6.0.inches

    val blueAmpNotePose = Translation2d(allianceNoteXCoordinateBlue, centerLine + (allianceNoteYSpacing * 2))
    val blueCenterNotePose = Translation2d(allianceNoteXCoordinateBlue, centerLine + allianceNoteYSpacing)
    val blueStageNotePose = Translation2d(allianceNoteXCoordinateBlue, centerLine)

    val centerNoteOnePose = Translation2d(midLine, centerLine + (centerNoteYSpacing * 2))
    val centerNoteTwoPose = Translation2d(midLine, centerLine + (centerNoteYSpacing * 1))
    val centerNoteThreePose = Translation2d(midLine, centerLine + (centerNoteYSpacing * 0))
    val centerNoteFourPose = Translation2d(midLine, centerLine + (centerNoteYSpacing * -1))
    val centerNoteFivePose = Translation2d(midLine, centerLine + (centerNoteYSpacing * -2))

    val speakerYAxis = centerLine + allianceNoteYSpacing

    fun Pose2d.onField() = (x >= 0 && x <= height.inMeters) && (y >= 0 || y <= width.inMeters)

    val speakerAimPose = Translation2d(0.35.meters, speakerYAxis)

    // This should be in the amp corner
    val feedAimPose = Translation2d(0.0.meters, width)

    val trapTags = listOf(14, 15, 16) // Blue
    val trapAimPoses = trapTags.map { tag -> aprilTagFieldLayout.getTagPose(tag).get().transformBy(Transform3d(1.0, 0.0, 0.0, Rotation3d(0.0, 0.0, Math.toRadians(180.0)))).toPose2d() }

    val feedPose = Translation2d(midLine + 1.0.meters, 1.0.meters).angleAtFeedCorner()

    fun getTrapAimPosition() = trapAimPoses.minBy { RobotPosition.distanceTo(it.translation).inMeters }

    /** Returns a [Pose2d] using this [Translation2d]'s coordinates, rotated to point at the corner where notes should be fed to. */
    private fun Translation2d.angleAtFeedCorner() = Pose2d(x, y, RobotPosition.angleTo(feedAimPose, currentPose = Translation2d(x, y)))
}