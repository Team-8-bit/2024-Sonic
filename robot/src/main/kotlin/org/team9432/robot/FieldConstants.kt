package org.team9432.robot

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.wpilibj.DriverStation
import org.team9432.Robot
import org.team9432.lib.util.PoseUtil

object FieldConstants {
    private val blueSpeakerPose = Pose2d(0.35, 5.40, Rotation2d())
    private val redSpeakerPose = PoseUtil.flip(blueSpeakerPose)

    val speakerPose
        get() = if (Robot.alliance == DriverStation.Alliance.Blue) blueSpeakerPose else redSpeakerPose
}