package org.team9432.robot

import edu.wpi.first.math.geometry.Pose2d
import org.team9432.lib.geometry.Translation2d
import org.team9432.lib.unit.feet
import org.team9432.lib.unit.inMeters
import org.team9432.lib.unit.inches
import org.team9432.lib.unit.meters

// All positions are on the blue side of the field and are flipped as needed
object FieldConstants {
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
    val speakerPose = Translation2d(0.35.meters, speakerYAxis)

    fun Pose2d.onField() = (x <= 0 || x >= height.inMeters) || (y <= 0 || y >= width.inMeters)
}