package org.team9432.robot.commands.drivetrain

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import org.littletonrobotics.junction.Logger
import org.team9432.lib.commandbased.KCommand
import org.team9432.robot.FieldConstants
import org.team9432.robot.subsystems.drivetrain.Drivetrain
import kotlin.math.atan2

class StaticSpeakerAlign: KCommand() {
    override val requirements = setOf(Drivetrain)

    override fun initialize() {
        Drivetrain.mode = Drivetrain.DrivetrainMode.STATIC_AIM
    }

    override fun execute() {
        val drivetrainPose = Drivetrain.getPose()
        val speakerPose = FieldConstants.speakerPose
        val robotRelativeSpeakerPoseX = speakerPose.x - drivetrainPose.x
        val robotRelativeSpeakerPoseY = speakerPose.y - drivetrainPose.y
        val angle = atan2(robotRelativeSpeakerPoseY, robotRelativeSpeakerPoseX)

        Logger.recordOutput("TargetPose", FieldConstants.speakerPose)

        Drivetrain.setAutoAlignGoal(Rotation2d(angle))
    }

    override fun end(interrupted: Boolean) {
        Logger.recordOutput("TargetPose", *emptyArray<Pose2d>())
        Drivetrain.stopAndX()
    }

    override fun isFinished(): Boolean {
        return false
    }
}