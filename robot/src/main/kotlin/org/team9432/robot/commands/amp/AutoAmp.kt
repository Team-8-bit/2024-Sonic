package org.team9432.robot.commands.amp

import org.team9432.lib.commandbased.commands.*
import org.team9432.robot.FieldConstants
import org.team9432.robot.commands.drivetrain.DriveSpeeds
import org.team9432.robot.commands.drivetrain.DriveToPosition

fun AutoAmp() = SequentialCommand(
    DriveToPosition(FieldConstants.ampAlignPose),
    ParallelRaceCommand(
        DriveSpeeds(0.0, 0.5, 0.0),
        ScoreAmp(5.0)
    )
)