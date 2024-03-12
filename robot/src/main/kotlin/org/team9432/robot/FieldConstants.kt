package org.team9432.robot

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.util.Units
import edu.wpi.first.wpilibj.DriverStation
import org.team9432.Robot
import org.team9432.lib.util.PoseUtil

// All positions are on the blue side of the field and are flipped as needed
object FieldConstants {

    val speakerPose get() = Pose2d(0.35, speakerYAxis, Rotation2d()).applyFlip()

    val width = Units.feetToMeters(26.0) + Units.inchesToMeters(11.25)
    val height = Units.feetToMeters(54.0) + Units.inchesToMeters(3.25)
    val centerLine = width / 2
    val midLine = height / 2

    private val allianceNoteXCoordinateBlue = Units.feetToMeters(9.0) + Units.inchesToMeters(6.0)
    private val allianceNoteXSpacing = Units.feetToMeters(4.0) + Units.inchesToMeters(9.0)

    val speakerYAxis = centerLine + allianceNoteXSpacing

    val blueAmpNotePose get() = Pose2d(allianceNoteXCoordinateBlue, centerLine + (allianceNoteXSpacing * 2), Rotation2d())
    val blueCenterNotePose get() = Pose2d(allianceNoteXCoordinateBlue, centerLine + allianceNoteXSpacing, Rotation2d())
    val blueStageNotePose get() = Pose2d(allianceNoteXCoordinateBlue, centerLine, Rotation2d())

    val ampNotePose get() = blueAmpNotePose.applyFlip()
    val centerNotePose get() = blueCenterNotePose.applyFlip()
    val stageNotePose get() = blueStageNotePose.applyFlip()

    fun Pose2d.applyFlip() = if (Robot.alliance == DriverStation.Alliance.Blue) this else PoseUtil.flip(this)
}