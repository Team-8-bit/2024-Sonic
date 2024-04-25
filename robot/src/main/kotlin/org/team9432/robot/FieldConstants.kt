package org.team9432.robot

import edu.wpi.first.apriltag.AprilTagFieldLayout
import edu.wpi.first.apriltag.AprilTagFields
import org.team9432.lib.constants.EvergreenFieldConstants.centerX
import org.team9432.lib.constants.EvergreenFieldConstants.centerY
import org.team9432.lib.unit.Translation2d
import org.team9432.lib.unit.feet
import org.team9432.lib.unit.inches

// All positions are on the blue side of the field and are flipped as needed
object FieldConstants {
    val aprilTagFieldLayout: AprilTagFieldLayout = AprilTagFields.k2024Crescendo.loadAprilTagLayoutField()


    /* -------- Note Positions -------- */

    /** X coordinate of the spike notes. */
    private val spikeNoteXCoordinate = 9.0.feet + 6.0.inches

    /** Y spacing between spike notes. */
    private val spikeNoteYSpacing = 4.0.feet + 9.0.inches

    /** Y spacing between center notes. */
    private val centerNoteYSpacing = 5.0.feet + 6.0.inches

    val ampSpikeNote = Translation2d(spikeNoteXCoordinate, centerY + (spikeNoteYSpacing * 2))
    val centerSpikeNote = Translation2d(spikeNoteXCoordinate, centerY + spikeNoteYSpacing)
    val stageSpikeNote = Translation2d(spikeNoteXCoordinate, centerY)

    val centerNoteOne = Translation2d(centerX, centerY + (centerNoteYSpacing * 2))
    val centerNoteTwo = Translation2d(centerX, centerY + (centerNoteYSpacing * 1))
    val centerNoteThree = Translation2d(centerX, centerY + (centerNoteYSpacing * 0))
    val centerNoteFour = Translation2d(centerX, centerY + (centerNoteYSpacing * -1))
    val centerNoteFive = Translation2d(centerX, centerY + (centerNoteYSpacing * -2))

    val speakerYCoordinate = centerY + spikeNoteYSpacing
}