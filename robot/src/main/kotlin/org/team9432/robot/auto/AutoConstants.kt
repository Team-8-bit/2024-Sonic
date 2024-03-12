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

    val centerNotePath get() = Pose2d(5.635, 6.381, Rotation2d(Math.PI)).applyFlip()
    val centerNoteShotPose get() = Pose2d(2.944, 6.375, Rotation2d(Math.PI)).applyFlip()

    val firstCenterNoteIntakePose get() = Pose2d(7.765, 7.436, Rotation2d(Math.PI)).applyFlip()
    val secondCenterNoteIntakePose get() = Pose2d(7.765, 5.742, Rotation2d(Math.PI)).applyFlip()

    private val targetNoteOffsetDistance = 0.8
    private val angledIntakeDistance = sqrt(targetNoteOffsetDistance.pow(2.0) / 2)

    private val blueAmpSideNoteIntakePose = FieldConstants.blueAmpNotePose.transformBy(Transform2d(Translation2d(0.0, -targetNoteOffsetDistance), Rotation2d.fromDegrees(-90.0)))
    private val blueAmpAngledNoteIntakePose = FieldConstants.blueAmpNotePose.transformBy(Transform2d(Translation2d(-angledIntakeDistance, -angledIntakeDistance), Rotation2d.fromDegrees(-135.0)))
    private val blueAmpNoteIntakePose = FieldConstants.blueAmpNotePose.transformBy(Transform2d(Translation2d(-targetNoteOffsetDistance, 0.0), Rotation2d.fromDegrees(180.0)))

    private val blueCenterNoteIntakePose = FieldConstants.blueCenterNotePose.transformBy(Transform2d(Translation2d(-0.75, 0.0), Rotation2d.fromDegrees(180.0)))
    private val blueCenterLeftNoteIntakePose = FieldConstants.blueCenterNotePose.transformBy(Transform2d(Translation2d(0.0, -targetNoteOffsetDistance), Rotation2d.fromDegrees(-90.0)))
    private val blueCenterRightIntakePose = FieldConstants.blueCenterNotePose.transformBy(Transform2d(Translation2d(0.0, targetNoteOffsetDistance), Rotation2d.fromDegrees(90.0)))

    private val blueStageSideNoteIntakePose = FieldConstants.blueStageNotePose.transformBy(Transform2d(Translation2d(0.0, targetNoteOffsetDistance), Rotation2d.fromDegrees(90.0)))
    private val blueStageAngledNoteIntakePose = FieldConstants.blueStageNotePose.transformBy(Transform2d(Translation2d(-angledIntakeDistance, angledIntakeDistance), Rotation2d.fromDegrees(135.0)))
    private val blueStageNoteIntakePose = FieldConstants.blueStageNotePose.transformBy(Transform2d(Translation2d(-targetNoteOffsetDistance, 0.0), Rotation2d.fromDegrees(180.0)))

    val ampSideIntakePose get() = blueAmpSideNoteIntakePose.applyFlip()
    val ampNoteAngledIntakePose get() = blueAmpAngledNoteIntakePose.applyFlip()
    val ampNoteIntakePose get() = blueAmpNoteIntakePose.applyFlip()

    val centerNoteIntakePose get() = blueCenterNoteIntakePose.applyFlip()
    val centerLeftNoteIntakePose get() = blueCenterLeftNoteIntakePose.applyFlip()
    val centerRightIntakePose get() = blueCenterRightIntakePose.applyFlip()

    val stageSideNoteIntakePose get() = blueStageSideNoteIntakePose.applyFlip()
    val stageNoteAngledIntakePose get() = blueStageAngledNoteIntakePose.applyFlip()
    val stageNoteIntakePose get() = blueStageNoteIntakePose.applyFlip()

    fun getIntakePosition(note: AllianceNote) = when (note) {
        AllianceNote.AMP -> listOf(ampNoteAngledIntakePose, ampNoteIntakePose, ampSideIntakePose).minBy { RobotPosition.distanceTo(it) }
        AllianceNote.CENTER -> listOf(centerNoteIntakePose, centerRightIntakePose, centerLeftNoteIntakePose).minBy { RobotPosition.distanceTo(it) }
        AllianceNote.STAGE -> listOf(stageNoteAngledIntakePose, stageNoteIntakePose, stageSideNoteIntakePose).minBy { RobotPosition.distanceTo(it) }
    }

    fun getNotePosition(note: AllianceNote) = when (note) {
        AllianceNote.AMP -> FieldConstants.ampNotePose
        AllianceNote.CENTER -> FieldConstants.centerNotePose
        AllianceNote.STAGE -> FieldConstants.stageNotePose
    }

    fun logPoses() {
        Logger.recordOutput("FourNoteFirstShotPose", fourNoteFirstShotPose)
        Logger.recordOutput(
            "IntakePosesBlue", *arrayOf(
                blueCenterLeftNoteIntakePose,
                blueCenterRightIntakePose,
                blueAmpSideNoteIntakePose,
                blueStageSideNoteIntakePose,
                blueAmpNoteIntakePose,
                blueAmpAngledNoteIntakePose,
                blueCenterNoteIntakePose,
                blueStageAngledNoteIntakePose,
                blueStageNoteIntakePose,
            )
        )
        Logger.recordOutput(
            "IntakePosesRed", *arrayOf(
                PoseUtil.flip(blueCenterLeftNoteIntakePose),
                PoseUtil.flip(blueCenterRightIntakePose),
                PoseUtil.flip(blueAmpSideNoteIntakePose),
                PoseUtil.flip(blueStageSideNoteIntakePose),
                PoseUtil.flip(blueAmpNoteIntakePose),
                PoseUtil.flip(blueAmpAngledNoteIntakePose),
                PoseUtil.flip(blueCenterNoteIntakePose),
                PoseUtil.flip(blueStageAngledNoteIntakePose),
                PoseUtil.flip(blueStageNoteIntakePose),
            )
        )
    }
}

enum class AllianceNote {
    AMP, CENTER, STAGE
}
