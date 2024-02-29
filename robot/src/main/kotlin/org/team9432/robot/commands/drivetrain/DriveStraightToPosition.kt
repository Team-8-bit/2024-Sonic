package org.team9432.robot.commands.drivetrain

import com.pathplanner.lib.path.*
import edu.wpi.first.math.geometry.Pose2d
import org.littletonrobotics.junction.Logger
import org.team9432.lib.commandbased.commands.SuppliedCommand
import org.team9432.robot.FieldConstants
import org.team9432.robot.subsystems.drivetrain.Drivetrain

fun DriveStraightToPosition(finalPose: Pose2d) = SuppliedCommand {
    val poses = listOf(Drivetrain.getPose(), finalPose)
    val trajectory = PathPlannerTrajectory(poses.map {
        val state = PathPlannerTrajectory.State()
        state.positionMeters = it.translation
        state.targetHolonomicRotation = it.rotation
        state
    })
    PathPlannerFollower(trajectory, allowFlip = false)
}