package org.team9432.robot.auto.commands

import org.team9432.lib.commandbased.commands.SequentialCommand
import org.team9432.robot.auto.Trajectories
import org.team9432.robot.commands.drivetrain.PathPlannerFollower

val testAuto = SequentialCommand(
    PathPlannerFollower(Trajectories.FIRST_NOTE),
    PathPlannerFollower(Trajectories.SECOND_NOTE),
    PathPlannerFollower(Trajectories.THIRD_NOTE),
)
