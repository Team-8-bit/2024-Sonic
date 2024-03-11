package org.team9432.robot.auto

import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.geometry.Transform2d
import edu.wpi.first.math.geometry.Translation2d
import org.littletonrobotics.junction.Logger
import org.team9432.lib.util.PoseUtil
import org.team9432.robot.FieldConstants
import org.team9432.robot.FieldConstants.applyFlip
import kotlin.math.pow
import kotlin.math.sqrt

object AutoConstants {
    fun logIntakePoses() {
        Logger.recordOutput("IntakePosesBlue", *arrayOf(blueAmpNoteIntakePose, blueCenterNoteIntakePose, blueStageNoteIntakePose))
        Logger.recordOutput("IntakePosesRed", *arrayOf(PoseUtil.flip(blueAmpNoteIntakePose), PoseUtil.flip(blueCenterNoteIntakePose), PoseUtil.flip(blueStageNoteIntakePose)))
    }

    // Math to make the distance to the note the same when aligned diagonally
    // x^2 + x^2 = a^2
    // x = 0.530
    // x^2 = 0.28125
    // x^2 = 0.5625 / 2
    // 2(x^2) = 0.5625
    // 2(x^2) = 0.75^2
    // 2(x^2) = a^2

    private val targetNoteOffsetDistance = 0.75
    private val angledIntakeDistance = sqrt(targetNoteOffsetDistance.pow(2.0) / 2)

    private val blueAmpNoteIntakePose = FieldConstants.blueAmpNotePose.transformBy(Transform2d(Translation2d(-angledIntakeDistance, -angledIntakeDistance), Rotation2d.fromDegrees(-135.0)))
    private val blueCenterNoteIntakePose = FieldConstants.blueCenterNotePose.transformBy(Transform2d(Translation2d(-targetNoteOffsetDistance, 0.0), Rotation2d.fromDegrees(180.0)))
    private val blueStageNoteIntakePose = FieldConstants.blueStageNotePose.transformBy(Transform2d(Translation2d(-angledIntakeDistance, angledIntakeDistance), Rotation2d.fromDegrees(135.0)))

    val ampNoteIntakePose get() = blueAmpNoteIntakePose.applyFlip()
    val centerNoteIntakePose get() = blueCenterNoteIntakePose.applyFlip()
    val stageNoteIntakePose get() = blueStageNoteIntakePose.applyFlip()

    fun getIntakePosition(note: AllianceNote) = when (note) {
        AllianceNote.AMP -> ampNoteIntakePose
        AllianceNote.CENTER -> centerNoteIntakePose
        AllianceNote.STAGE -> stageNoteIntakePose
    }
    fun getNotePosition(note: AllianceNote) = when (note) {
        AllianceNote.AMP -> FieldConstants.ampNotePose
        AllianceNote.CENTER -> FieldConstants.centerNotePose
        AllianceNote.STAGE -> FieldConstants.stageNotePose
    }
}

enum class AllianceNote {
    AMP, CENTER, STAGE
}
