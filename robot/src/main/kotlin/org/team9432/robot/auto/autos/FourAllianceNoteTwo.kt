package org.team9432.robot.auto.autos

import edu.wpi.first.math.geometry.Rotation2d
import org.team9432.lib.commandbased.KCommand
import org.team9432.lib.commandbased.commands.ParallelCommand
import org.team9432.lib.commandbased.commands.ParallelDeadlineCommand
import org.team9432.lib.commandbased.commands.SequentialCommand
import org.team9432.lib.commandbased.commands.WaitCommand
import org.team9432.robot.FieldConstants
import org.team9432.robot.MechanismSide
import org.team9432.robot.auto.AllianceNote
import org.team9432.robot.auto.AutoConstants
import org.team9432.robot.auto.commands.*
import org.team9432.robot.auto.subsections.AlignToIntakeNote
import org.team9432.robot.auto.subsections.IntakeNote
import org.team9432.robot.commands.drivetrain.DriveSpeeds
import org.team9432.robot.commands.drivetrain.DriveToPosition
import org.team9432.robot.commands.drivetrain.TargetDriveSpeeds
import org.team9432.robot.commands.hopper.MoveToSide
import org.team9432.robot.commands.intake.FinishIntakingAndAlign

fun FourAllianceNoteTwo() = SequentialCommand(
    InitAuto(Rotation2d(Math.PI)),
    ParallelCommand(
        CollectPreloadAndStartShooter(),
        DriveToPosition(AutoConstants.fourNoteFirstShotPose)
    ),
    ShootFromHopper(),
    IntakeNote(AllianceNote.STAGE),
    FinishIntakingThen(AlignToIntakeNote(AllianceNote.CENTER)),
    AutoShoot(driveCloser = false),
    IntakeNote(AllianceNote.CENTER),
    FinishIntakingThen(AlignToIntakeNote(AllianceNote.AMP)),
    AutoShoot(driveCloser = false),
    IntakeNote(AllianceNote.AMP),
    ParallelDeadlineCommand(
        TargetDriveSpeeds(vx = -3.0, vy = -3.0) { FieldConstants.speakerPose },
        deadline = FinishIntakingAndLoadToSpeaker()
    ),
    AutoShoot(driveCloser = false),
    ExitAuto(),
)

fun FinishIntakingThen(command: KCommand, delay: Double = 0.5) = ParallelCommand(
    SequentialCommand(
        WaitCommand(delay),
        command
    ),
    FinishIntakingAndLoadToSpeaker()
)

fun FinishIntakingAndLoadToSpeaker() = SequentialCommand(
    FinishIntakingAndAlign(),
    MoveToSide(MechanismSide.SPEAKER)
)