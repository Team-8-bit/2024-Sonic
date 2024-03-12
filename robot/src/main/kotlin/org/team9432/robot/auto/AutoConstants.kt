package org.team9432.robot.auto

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.geometry.Transform2d
import edu.wpi.first.math.geometry.Translation2d
import org.littletonrobotics.junction.Logger
import org.team9432.lib.util.PoseUtil
import org.team9432.robot.FieldConstants
import org.team9432.robot.FieldConstants.applyFlip
import org.team9432.robot.subsystems.RobotPosition
import kotlin.math.pow
import kotlin.math.sqrt

object AutoConstants {
    val fourNoteFirstShotPose get() = Pose2d(2.359, 4.418, Rotation2d.fromDegrees(152.5214)).applyFlip()

    private val targetNoteOffsetDistance = 1.0
    private val angledIntakeDistance = sqrt(targetNoteOffsetDistance.pow(2.0) / 2)

    private val blueAmpAngledNoteIntakePose = FieldConstants.blueAmpNotePose.transformBy(Transform2d(Translation2d(-angledIntakeDistance, -angledIntakeDistance), Rotation2d.fromDegrees(-135.0)))
    private val blueAmpNoteIntakePose = FieldConstants.blueAmpNotePose.transformBy(Transform2d(Translation2d(-targetNoteOffsetDistance, 0.0), Rotation2d.fromDegrees(180.0)))
    private val blueCenterNoteIntakePose = FieldConstants.blueCenterNotePose.transformBy(Transform2d(Translation2d(-0.75, 0.0), Rotation2d.fromDegrees(180.0)))
    private val blueStageAngledNoteIntakePose = FieldConstants.blueStageNotePose.transformBy(Transform2d(Translation2d(-angledIntakeDistance, angledIntakeDistance), Rotation2d.fromDegrees(135.0)))
    private val blueStageNoteIntakePose = FieldConstants.blueStageNotePose.transformBy(Transform2d(Translation2d(-targetNoteOffsetDistance, 0.0), Rotation2d.fromDegrees(180.0)))

    val ampNoteAngledIntakePose get() = blueAmpAngledNoteIntakePose.applyFlip()
    val ampNoteIntakePose get() = blueAmpNoteIntakePose.applyFlip()
    val centerNoteIntakePose get() = blueCenterNoteIntakePose.applyFlip()
    val stageNoteAngledIntakePose get() = blueStageAngledNoteIntakePose.applyFlip()
    val stageNoteIntakePose get() = blueStageNoteIntakePose.applyFlip()

    fun getIntakePosition(note: AllianceNote) = when (note) {
        AllianceNote.AMP -> listOf(ampNoteAngledIntakePose, ampNoteIntakePose).minBy { RobotPosition.distanceTo(it) }
        AllianceNote.CENTER -> centerNoteIntakePose
        AllianceNote.STAGE -> listOf(stageNoteAngledIntakePose, stageNoteIntakePose).minBy { RobotPosition.distanceTo(it) }
    }

    fun getNotePosition(note: AllianceNote) = when (note) {
        AllianceNote.AMP -> FieldConstants.ampNotePose
        AllianceNote.CENTER -> FieldConstants.centerNotePose
        AllianceNote.STAGE -> FieldConstants.stageNotePose
    }

    fun logPoses() {
        Logger.recordOutput("FourNoteFirstShotPose", fourNoteFirstShotPose)
        Logger.recordOutput("IntakePosesBlue", *arrayOf(
            blueAmpNoteIntakePose,
            blueAmpAngledNoteIntakePose,
            blueCenterNoteIntakePose,
            blueStageAngledNoteIntakePose,
            blueStageNoteIntakePose,
        ))
        Logger.recordOutput("IntakePosesRed", *arrayOf(
            PoseUtil.flip(blueAmpNoteIntakePose),
            PoseUtil.flip(blueAmpAngledNoteIntakePose),
            PoseUtil.flip(blueCenterNoteIntakePose),
            PoseUtil.flip(blueStageAngledNoteIntakePose),
            PoseUtil.flip(blueStageNoteIntakePose),
        ))
    }
}

enum class AllianceNote {
    AMP, CENTER, STAGE
}
