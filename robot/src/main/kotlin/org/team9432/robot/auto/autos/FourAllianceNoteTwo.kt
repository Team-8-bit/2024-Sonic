package org.team9432.robot.auto.autos

import edu.wpi.first.math.geometry.Rotation2d
import org.team9432.lib.commandbased.commands.*
import org.team9432.robot.FieldConstants
import org.team9432.robot.RobotState
import org.team9432.robot.auto.AllianceNote
import org.team9432.robot.auto.AutoConstants
import org.team9432.robot.auto.commands.*
import org.team9432.robot.auto.subsections.AlignToIntakeNote
import org.team9432.robot.auto.subsections.IntakeNote
import org.team9432.robot.commands.drivetrain.DriveToPosition
import org.team9432.robot.commands.drivetrain.TargetDriveSpeeds
import org.team9432.robot.commands.hood.HoodAimAtSpeaker
import org.team9432.robot.subsystems.drivetrain.Drivetrain

fun FourAllianceNoteTwo() = SequentialCommand(
    InitAuto(Rotation2d(Math.PI)),
    ParallelCommand(
        CollectPreloadAndStartShooter(),
        DriveToPosition(AutoConstants.fourNoteFirstShotPose)
    ),
    ShootFromHopper(),
    IntakeNote(AllianceNote.STAGE),
    FinishIntakingThen(DriveToPosition(AutoConstants.centerNoteIntakePose)),
    AutoShoot(driveCloser = false),
    IntakeNote(AllianceNote.CENTER),
    FinishIntakingThen(AlignToIntakeNote(AllianceNote.AMP)),
    InstantCommand { RobotState.autoIsUsingApriltags = false },
    AutoShoot(driveCloser = false),
    IntakeNote(AllianceNote.AMP),
    ParallelDeadlineCommand(
        ParallelDeadlineCommand(
            TargetDriveSpeeds(vx = 3.0, vy = 3.0 * Drivetrain.coordinateFlip) { FieldConstants.speakerPose },
            deadline = WaitCommand(0.75)
        ),
        deadline = FinishIntakingAndLoadToSpeaker()
    ),
    AutoShoot(driveCloser = false),
    InstantCommand { RobotState.autoIsUsingApriltags = true },
    ExitAuto(),
)