package org.team9432.robot.auto

import com.pathplanner.lib.path.PathPlannerPath
import com.pathplanner.lib.path.PathPlannerTrajectory
import org.team9432.lib.wpilib.ChassisSpeeds


object Trajectories {
    val FIRST_NOTE = getTrajectory("First Note")
    val SECOND_NOTE = getTrajectory("Second Note")
    val THIRD_NOTE = getTrajectory("Third Note")


    private fun getTrajectory(name: String?): PathPlannerTrajectory {
        val path = PathPlannerPath.fromPathFile(name)
        val startingPosition = path.previewStartingHolonomicPose
        return PathPlannerTrajectory(path, ChassisSpeeds(), startingPosition.rotation)
    }
}