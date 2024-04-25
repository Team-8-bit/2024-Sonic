package org.team9432.robot.auto

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.geometry.Translation2d
import org.team9432.lib.constants.EvergreenFieldConstants
import org.team9432.lib.unit.*
import org.team9432.robot.FieldConstants
import org.team9432.robot.PositionConstants
import org.team9432.robot.RobotPosition
import kotlin.math.pow
import kotlin.math.sqrt

object AutoConstants {

    /* -------- Spike Note Positions -------- */

    // Offsets for collecting the spike notes
    private val targetNoteOffsetDistance = 0.8.meters
    private val angledIntakeDistance = sqrt(targetNoteOffsetDistance.inMeters.pow(2) / 2).meters

    // Positions for collecting the amp spike note
    val ampNoteAngledIntakePose = Pose2d(FieldConstants.ampSpikeNote.plus(Translation2d(-angledIntakeDistance, -angledIntakeDistance)), -135.0.degrees)
    val ampNoteIntakePose = Pose2d(FieldConstants.ampSpikeNote.plus(Translation2d(-targetNoteOffsetDistance, 0.0.meters)), 180.0.degrees)

    // Position for collecting the center spike note
    val centerNoteIntakePose = Pose2d(FieldConstants.centerSpikeNote.plus(Translation2d(-0.75.meters, 0.0.meters)), 180.0.degrees)

    // Positions for collecting the stage spike note
    val stageNoteAngledIntakePose = Pose2d(FieldConstants.stageSpikeNote.plus(Translation2d(-angledIntakeDistance, angledIntakeDistance)), 135.0.degrees)
    val stageNoteIntakePose = Pose2d(FieldConstants.stageSpikeNote.plus(Translation2d(-targetNoteOffsetDistance, 0.0.meters)), 180.0.degrees)


    /* -------- Center Note Positions -------- */

    // Position the robot should line up at to collect a center note
    private val centerNoteOffsetDistance = 0.6.meters
    val centerNoteOneIntakePose = Pose2d(FieldConstants.centerNoteOne.plus(Translation2d(-centerNoteOffsetDistance, 0.0.meters)), 180.0.degrees)
    val centerNoteTwoIntakePose = Pose2d(FieldConstants.centerNoteTwo.plus(Translation2d(-centerNoteOffsetDistance, 0.0.meters)), 180.0.degrees)
    val centerNoteThreeIntakePose = Pose2d(FieldConstants.centerNoteThree.plus(Translation2d(-centerNoteOffsetDistance, 0.0.meters)), 180.0.degrees)
    val centerNoteFourIntakePose = Pose2d(FieldConstants.centerNoteFour.plus(Translation2d(-centerNoteOffsetDistance, 0.0.meters)), 180.0.degrees)
    val centerNoteFiveIntakePose = Pose2d(FieldConstants.centerNoteFive.plus(Translation2d(-centerNoteOffsetDistance, 0.0.meters)), 180.0.degrees)


    /* -------- Auto Path Positions -------- */

    // Positions to drive to for four spike note autos
    val fourNoteFirstShotPose = Translation2d(2.359.meters, 4.418.meters).angleAtSpeaker()
    val fourNoteFirstShotPoseReversed = Translation2d(2.159.meters, 6.617.meters).angleAtSpeaker()

    // Positions to drive to for the centerline auto
    val centerStage = Pose2d(EvergreenFieldConstants.centerX - 3.5.meters, EvergreenFieldConstants.centerY, Rotation2d(Math.PI))
    val centerCenterShot = Translation2d(2.0, 4.0).angleAtSpeaker()
    val centerCenterDriveOne = Pose2d(3.5, 2.5, Rotation2d(Math.PI))


    /* -------- Misc. Utilities -------- */

    /** Gets the closest intaking position for a given note. */
    fun getClosestIntakePosition(note: SpikeNote) = when (note) {
        SpikeNote.AMP -> listOf(ampNoteAngledIntakePose, ampNoteIntakePose).minBy { RobotPosition.distanceTo(it.translation).inMeters }
        SpikeNote.CENTER -> listOf(centerNoteIntakePose).minBy { RobotPosition.distanceTo(it.translation).inMeters }
        SpikeNote.STAGE -> listOf(stageNoteAngledIntakePose, stageNoteIntakePose).minBy { RobotPosition.distanceTo(it.translation).inMeters }
    }

    /** Get position of a given note. */
    fun getNotePosition(note: SpikeNote) = when (note) {
        SpikeNote.AMP -> FieldConstants.ampSpikeNote
        SpikeNote.CENTER -> FieldConstants.centerSpikeNote
        SpikeNote.STAGE -> FieldConstants.stageSpikeNote
    }

    /** Returns a [Pose2d] using this [Translation2d]'s coordinates, rotated to point at the speaker. */
    private fun Translation2d.angleAtSpeaker() = Pose2d(x, y, RobotPosition.angleTo(PositionConstants.speakerAimPose, currentPose = Translation2d(x, y)))
}

enum class SpikeNote {
    AMP, CENTER, STAGE
}
