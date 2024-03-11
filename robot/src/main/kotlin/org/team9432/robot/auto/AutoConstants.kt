package org.team9432.robot.auto

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.wpilibj.DriverStation.Alliance
import org.team9432.Robot
import org.team9432.lib.util.PoseUtil

object AutoConstants {
    private val redAmpNoteIntakePose = Pose2d(14.45, 5.50 + (5.50 - 3.93), Rotation2d())
    private val redCenterNoteIntakePose = Pose2d(14.45, 5.50, Rotation2d())
    private val redStageNoteIntakePose = Pose2d(14.45, 3.93, Rotation2d())
    private val blueAmpNoteIntakePose = PoseUtil.flip(redAmpNoteIntakePose)
    private val blueCenterNoteIntakePose = PoseUtil.flip(redCenterNoteIntakePose)
    private val blueStageNoteIntakePose = PoseUtil.flip(redStageNoteIntakePose)

    val ampNoteIntakePose get() = if (Robot.alliance == Alliance.Red) redAmpNoteIntakePose else blueAmpNoteIntakePose
    val centerNoteIntakePose get() = if (Robot.alliance == Alliance.Red) redCenterNoteIntakePose else blueCenterNoteIntakePose
    val stageNoteIntakePose get() = if (Robot.alliance == Alliance.Red) redStageNoteIntakePose else blueStageNoteIntakePose
}