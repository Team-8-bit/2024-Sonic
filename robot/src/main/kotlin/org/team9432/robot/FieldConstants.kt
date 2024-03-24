package org.team9432.robot

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.util.Units

// All positions are on the blue side of the field and are flipped as needed
object FieldConstants {
    val width = Units.feetToMeters(26.0) + Units.inchesToMeters(11.25)
    val height = Units.feetToMeters(54.0) + Units.inchesToMeters(3.25)
    val centerLine = width / 2
    val midLine = height / 2

    private val allianceNoteXCoordinateBlue = Units.feetToMeters(9.0) + Units.inchesToMeters(6.0)
    private val allianceNoteYSpacing = Units.feetToMeters(4.0) + Units.inchesToMeters(9.0)
    private val centerNoteYSpacing = Units.feetToMeters(5.0) + Units.inchesToMeters(6.0)

    val blueAmpNotePose = Pose2d(allianceNoteXCoordinateBlue, centerLine + (allianceNoteYSpacing * 2), Rotation2d())
    val blueCenterNotePose = Pose2d(allianceNoteXCoordinateBlue, centerLine + allianceNoteYSpacing, Rotation2d())
    val blueStageNotePose = Pose2d(allianceNoteXCoordinateBlue, centerLine, Rotation2d())

    val centerNoteOnePose = Pose2d(midLine, centerLine + (centerNoteYSpacing * 2), Rotation2d())
    val centerNoteTwoPose = Pose2d(midLine, centerLine + (centerNoteYSpacing * 1), Rotation2d())
    val centerNoteThreePose = Pose2d(midLine, centerLine + (centerNoteYSpacing * 0), Rotation2d())
    val centerNoteFourPose = Pose2d(midLine, centerLine + (centerNoteYSpacing * -1), Rotation2d())
    val centerNoteFivePose = Pose2d(midLine, centerLine + (centerNoteYSpacing * -2), Rotation2d())

    val speakerYAxis = centerLine + allianceNoteYSpacing
    val speakerPose = Pose2d(0.35, speakerYAxis, Rotation2d())
}