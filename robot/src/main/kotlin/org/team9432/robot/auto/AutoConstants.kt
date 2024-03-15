package org.team9432.robot.auto

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.geometry.Transform2d
import edu.wpi.first.math.geometry.Translation2d
import org.team9432.robot.FieldConstants
import org.team9432.robot.subsystems.RobotPosition
import kotlin.math.pow
import kotlin.math.sqrt

object AutoConstants {
    val fourNoteFirstShotPose = Pose2d(2.359, 4.418, Rotation2d.fromDegrees(152.5214))

    val topCenterNotePath = Pose2d(5.635, 6.381, Rotation2d(Math.PI))
    val topCenterNoteShotPose = Pose2d(2.944, 6.375, Rotation2d(Math.PI))

    val firstCenterNoteIntakePose = Pose2d(7.765, 7.436, Rotation2d(Math.PI))
    val secondCenterNoteIntakePose = Pose2d(7.765, 5.742, Rotation2d(Math.PI))

    private val targetNoteOffsetDistance = 0.8
    private val angledIntakeDistance = sqrt(targetNoteOffsetDistance.pow(2.0) / 2)

    val ampNoteAngledIntakePose = FieldConstants.blueAmpNotePose.transformBy(Transform2d(Translation2d(-angledIntakeDistance, -angledIntakeDistance), Rotation2d.fromDegrees(-135.0)))
    val ampNoteIntakePose = FieldConstants.blueAmpNotePose.transformBy(Transform2d(Translation2d(-targetNoteOffsetDistance, 0.0), Rotation2d.fromDegrees(180.0)))

    val centerNoteIntakePose = FieldConstants.blueCenterNotePose.transformBy(Transform2d(Translation2d(-0.75, 0.0), Rotation2d.fromDegrees(180.0)))

    val stageNoteAngledIntakePose = FieldConstants.blueStageNotePose.transformBy(Transform2d(Translation2d(-angledIntakeDistance, angledIntakeDistance), Rotation2d.fromDegrees(135.0)))
    val stageNoteIntakePose = FieldConstants.blueStageNotePose.transformBy(Transform2d(Translation2d(-targetNoteOffsetDistance, 0.0), Rotation2d.fromDegrees(180.0)))

    fun getIntakePosition(note: AllianceNote) = when (note) {
        AllianceNote.AMP -> listOf(ampNoteAngledIntakePose, ampNoteIntakePose).minBy { RobotPosition.distanceTo(it) }
        AllianceNote.CENTER -> listOf(centerNoteIntakePose).minBy { RobotPosition.distanceTo(it) }
        AllianceNote.STAGE -> listOf(stageNoteAngledIntakePose, stageNoteIntakePose).minBy { RobotPosition.distanceTo(it) }
    }

    fun getNotePosition(note: AllianceNote) = when (note) {
        AllianceNote.AMP -> FieldConstants.blueAmpNotePose
        AllianceNote.CENTER -> FieldConstants.blueCenterNotePose
        AllianceNote.STAGE -> FieldConstants.blueStageNotePose
    }
}

enum class AllianceNote {
    AMP, CENTER, STAGE
}
