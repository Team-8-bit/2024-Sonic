package org.team9432.robot.auto

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.geometry.Translation2d
import org.littletonrobotics.junction.Logger
import org.team9432.lib.geometry.Pose2d
import org.team9432.lib.geometry.Translation2d
import org.team9432.lib.unit.degrees
import org.team9432.lib.unit.inMeters
import org.team9432.lib.unit.meters
import org.team9432.robot.FieldConstants
import org.team9432.robot.RobotPosition
import kotlin.math.pow
import kotlin.math.sqrt

object AutoConstants {
    val fourNoteFirstShotPose = Translation2d(2.359.meters, 4.418.meters).angleAtSpeaker()
    val fourNoteFirstShotPoseReversed = Translation2d(2.159.meters, 6.617.meters).angleAtSpeaker()

    val topCenterNotePath = Pose2d(5.635.meters, 6.381.meters, 180.0.degrees)
    val topCenterNoteShotPose = Pose2d(2.944.meters, 6.375.meters, 0.0.degrees).angleAtSpeaker()

    val bottomCenterNotePath = Pose2d(5.326.meters, 1.628.meters, 180.0.degrees)
    val bottomCenterNoteShotPose = Pose2d(2.0.meters, 3.5.meters, 0.0.degrees).angleAtSpeaker()

    private val targetNoteOffsetDistance = 0.8.meters
    private val angledIntakeDistance = sqrt(targetNoteOffsetDistance.inMeters.pow(2) / 2).meters

    val ampNoteAngledIntakePose = Pose2d(FieldConstants.blueAmpNotePose.plus(Translation2d(-angledIntakeDistance, -angledIntakeDistance)), -135.0.degrees)
    val ampNoteIntakePose = Pose2d(FieldConstants.blueAmpNotePose.plus(Translation2d(-targetNoteOffsetDistance, 0.0.meters)), 180.0.degrees)

    val centerNoteIntakePose = Pose2d(FieldConstants.blueCenterNotePose.plus(Translation2d(-0.75.meters, 0.0.meters)), 180.0.degrees)

    val stageNoteAngledIntakePose = Pose2d(FieldConstants.blueStageNotePose.plus(Translation2d(-angledIntakeDistance, angledIntakeDistance)), 135.0.degrees)
    val stageNoteIntakePose = Pose2d(FieldConstants.blueStageNotePose.plus(Translation2d(-targetNoteOffsetDistance, 0.0.meters)), 180.0.degrees)

    private val centerNoteOffsetDistance = 0.6.meters
    val centerNoteOneIntakePose = Pose2d(FieldConstants.centerNoteOnePose.plus(Translation2d(-centerNoteOffsetDistance, 0.0.meters)), 180.0.degrees)
    val centerNoteTwoIntakePose = Pose2d(FieldConstants.centerNoteTwoPose.plus(Translation2d(-centerNoteOffsetDistance, 0.0.meters)), 180.0.degrees)
    val centerNoteThreeIntakePose = Pose2d(FieldConstants.centerNoteThreePose.plus(Translation2d(-centerNoteOffsetDistance, 0.0.meters)), 180.0.degrees)
    val centerNoteFourIntakePose = Pose2d(FieldConstants.centerNoteFourPose.plus(Translation2d(-centerNoteOffsetDistance, 0.0.meters)), 180.0.degrees)
    val centerNoteFiveIntakePose = Pose2d(FieldConstants.centerNoteFivePose.plus(Translation2d(-centerNoteOffsetDistance, 0.0.meters)), 180.0.degrees)

    val centerStage = Pose2d(FieldConstants.midLine - 3.5.meters, FieldConstants.centerLine, Rotation2d(Math.PI))
    val centerCenterShot = Translation2d(2.0, 3.5).angleAtSpeaker()
    val centerCenterDriveOne = Pose2d(3.5, 3.0, Rotation2d(Math.PI))

    fun getIntakePosition(note: AllianceNote) = when (note) {
        AllianceNote.AMP -> listOf(ampNoteAngledIntakePose, ampNoteIntakePose).minBy { RobotPosition.distanceTo(it.translation) }
        AllianceNote.CENTER -> listOf(centerNoteIntakePose).minBy { RobotPosition.distanceTo(it.translation) }
        AllianceNote.STAGE -> listOf(stageNoteAngledIntakePose, stageNoteIntakePose).minBy { RobotPosition.distanceTo(it.translation) }
    }

    fun getNotePosition(note: AllianceNote) = when (note) {
        AllianceNote.AMP -> FieldConstants.blueAmpNotePose
        AllianceNote.CENTER -> FieldConstants.blueCenterNotePose
        AllianceNote.STAGE -> FieldConstants.blueStageNotePose
    }

    private fun Pose2d.angleAtSpeaker() = Pose2d(x, y, RobotPosition.angleTo(FieldConstants.speakerAimPose, currentPose = Translation2d(x, y)))
    private fun Translation2d.angleAtSpeaker() = Pose2d(x, y, RobotPosition.angleTo(FieldConstants.speakerAimPose, currentPose = Translation2d(x, y)))
}

enum class AllianceNote {
    AMP, CENTER, STAGE
}
