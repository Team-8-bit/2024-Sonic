package org.team9432.robot.commands.drivetrain

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import org.littletonrobotics.junction.Logger
import org.team9432.lib.commandbased.KCommand
import org.team9432.lib.util.PoseUtil
import org.team9432.robot.FieldConstants
import org.team9432.robot.subsystems.drivetrain.Drivetrain
import kotlin.math.atan2

// This doesn't take robot speed into account yet
class MobileSpeakerAlign: KCommand() {
    override val requirements = setOf(Drivetrain)

    override fun initialize() {
        Drivetrain.mode = Drivetrain.DrivetrainMode.SHOOT_DRIVE
    }

    override fun execute() {
        val drivetrainPose = Drivetrain.getPose()
        val speakerPose = FieldConstants.speakerPose

        Logger.recordOutput("TargetPose", FieldConstants.speakerPose)

        Drivetrain.setAutoAlignGoal(PoseUtil.angleBetween(drivetrainPose, speakerPose))
    }

    override fun end(interrupted: Boolean) {
        Logger.recordOutput("TargetPose", *emptyArray<Pose2d>())
    }

    override fun isFinished(): Boolean {
        return Drivetrain.mode != Drivetrain.DrivetrainMode.SHOOT_DRIVE
    }
}