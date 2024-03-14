package org.team9432.robot.auto.autos

import edu.wpi.first.math.geometry.Rotation2d
import org.team9432.lib.commandbased.commands.ParallelCommand
import org.team9432.lib.commandbased.commands.SequentialCommand
import org.team9432.robot.auto.AutoConstants
import org.team9432.robot.auto.commands.*
import org.team9432.robot.auto.subsections.IntakeNote
import org.team9432.robot.commands.drivetrain.DriveToPosition

fun BottomTwoCenterNote() = SequentialCommand(
    InitAuto(Rotation2d(Math.PI)),
    // Shoot the preload
    ParallelCommand(
        CollectPreloadAndStartShooter(),
        DriveToPosition(AutoConstants.topCenterNoteShotPose)
    ),
    ShootFromHopper(),

    // Drive to the center and collect the top note
    IntakeNote(AutoConstants.firstCenterNoteIntakePose, timeout = 0.5),
    FinishIntakingThen(DriveToPosition(AutoConstants.topCenterNoteShotPose)),
    AutoShoot(driveCloser = false),

    // Collect the next center note
    DriveToPosition(AutoConstants.topCenterNotePath, positionalTolerance = 1.0),
    IntakeNote(AutoConstants.secondCenterNoteIntakePose, timeout = 0.5),
    FinishIntakingThen(
        SequentialCommand(
            DriveToPosition(AutoConstants.topCenterNotePath, positionalTolerance = 1.0),
            DriveToPosition(AutoConstants.topCenterNoteShotPose)
        )
    ),
    AutoShoot(driveCloser = false),

    ExitAuto(),
)