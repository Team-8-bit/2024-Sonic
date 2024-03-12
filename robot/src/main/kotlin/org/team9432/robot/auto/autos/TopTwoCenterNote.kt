package org.team9432.robot.auto.autos

import edu.wpi.first.math.geometry.Rotation2d
import org.team9432.lib.commandbased.commands.ParallelCommand
import org.team9432.lib.commandbased.commands.ParallelDeadlineCommand
import org.team9432.lib.commandbased.commands.SequentialCommand
import org.team9432.robot.FieldConstants
import org.team9432.robot.auto.AllianceNote
import org.team9432.robot.auto.AutoConstants
import org.team9432.robot.auto.commands.*
import org.team9432.robot.auto.subsections.AlignToIntakeNote
import org.team9432.robot.auto.subsections.IntakeNote
import org.team9432.robot.commands.drivetrain.DriveToPosition
import org.team9432.robot.commands.drivetrain.TargetDriveSpeeds

fun TopTwoCenterNote() = SequentialCommand(
    InitAuto(Rotation2d(Math.PI)),
    // Shoot the preload
    ParallelCommand(
        CollectPreloadAndStartShooter(),
        DriveToPosition(AutoConstants.centerNoteShotPose)
    ),
    ShootFromHopper(),

    // Drive to the center and collect the top note
    IntakeNote(AutoConstants.firstCenterNoteIntakePose, timeout = 0.5),
    FinishIntakingThen(DriveToPosition(AutoConstants.centerNoteShotPose)),
    AutoShoot(driveCloser = false),

    // Collect the next center note
    DriveToPosition(AutoConstants.centerNotePath, positionalTolerance = 1.0),
    IntakeNote(AutoConstants.secondCenterNoteIntakePose, timeout = 0.5),
    FinishIntakingThen(
        SequentialCommand(
            DriveToPosition(AutoConstants.centerNotePath, positionalTolerance = 1.0),
            DriveToPosition(AutoConstants.centerNoteShotPose)
        )
    ),
    AutoShoot(driveCloser = false),

    ExitAuto(),
)