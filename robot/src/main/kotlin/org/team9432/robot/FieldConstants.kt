package org.team9432.robot

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.wpilibj.DriverStation
import org.team9432.Robot
import org.team9432.lib.util.PoseUtil

object FieldConstants {
    private val blueSpeakerPose = Pose2d(0.0, 5.575, Rotation2d())
    private val redSpeakerPose = PoseUtil.flip(blueSpeakerPose)

    private val blueAmpPose = Pose2d(1.86, 7.78, Rotation2d.fromDegrees(-90.0))
    private val redAmpPose = PoseUtil.flip(blueAmpPose)

    private val redAmpAlignPose = Pose2d(14.675, 7.532, Rotation2d.fromDegrees(-90.0))
    private val blueAmpAlignPose = PoseUtil.flip(redAmpAlignPose)

    val speakerPose
        get() = if (Robot.alliance == DriverStation.Alliance.Blue) blueSpeakerPose else redSpeakerPose

    val ampPose
        get() = if (Robot.alliance == DriverStation.Alliance.Blue) blueAmpPose else redAmpPose

    val ampAlignPose
        get() = if (Robot.alliance == DriverStation.Alliance.Blue) blueAmpAlignPose else redAmpAlignPose
}