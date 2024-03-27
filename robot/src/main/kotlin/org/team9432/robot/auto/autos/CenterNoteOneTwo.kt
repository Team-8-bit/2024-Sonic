package org.team9432.robot.auto.autos

import edu.wpi.first.math.geometry.Rotation2d
import org.team9432.lib.commandbased.commands.ParallelCommand
import org.team9432.lib.commandbased.commands.SequentialCommand
import org.team9432.robot.auto.AutoConstants
import org.team9432.robot.auto.commands.*
import org.team9432.robot.auto.subsections.IntakeNote
import org.team9432.robot.commands.drivetrain.DriveToPosition

fun CenterNoteOneTwo() = SequentialCommand(
    InitAuto(Rotation2d(Math.PI)),
    // Shoot the preload
    ParallelCommand(
        CollectPreloadAndStartShooter(),
        DriveToPosition(AutoConstants.topCenterNoteShotPose)
    ),
    AutoShoot(),
    // Drive to the center and collect the top note
    IntakeNote(AutoConstants.centerNoteOneIntakePose, timeout = 0.5),
    FinishIntakingThen(DriveToPosition(AutoConstants.topCenterNoteShotPose)),
    AutoShoot(),

    // Collect the next center note
    DriveToPosition(AutoConstants.topCenterNotePath, velocityGoal = 4.0),
    IntakeNote(AutoConstants.centerNoteTwoIntakePose, timeout = 0.5),
    FinishIntakingThen(
        SequentialCommand(
            DriveToPosition(AutoConstants.topCenterNotePath, velocityGoal = 4.0),
            DriveToPosition(AutoConstants.topCenterNoteShotPose)
        )
    ),
    AutoShoot(),
    ExitAuto(),
)