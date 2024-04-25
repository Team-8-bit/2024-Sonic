package org.team9432.robot.auto.autos

import edu.wpi.first.math.geometry.Rotation2d
import org.team9432.lib.commandbased.commands.ParallelCommand
import org.team9432.lib.commandbased.commands.ParallelDeadlineCommand
import org.team9432.lib.commandbased.commands.SequentialCommand
import org.team9432.lib.commandbased.commands.WaitUntilCommand
import org.team9432.robot.auto.AllianceNote
import org.team9432.robot.auto.AutoConstants
import org.team9432.robot.auto.builder.subsections.AlignToIntakeNote
import org.team9432.robot.auto.builder.subsections.IntakeNote
import org.team9432.robot.commands.drivetrain.DriveToPosition
import org.team9432.robot.commands.hopper.PullFromSpeakerShooter
import org.team9432.robot.commands.shooter.AutoShoot
import org.team9432.robot.subsystems.Hood
import org.team9432.robot.subsystems.Shooter

fun FourAllianceNote() = SequentialCommand(
    InitAuto(Rotation2d.fromDegrees(180.0)),
    ParallelDeadlineCommand(
        Hood.Commands.aimAtSpeaker(),
        Shooter.Commands.runAtSpeeds(),
        deadline = SequentialCommand(
            ParallelCommand(
                PullFromSpeakerShooter(),
                WaitUntilCommand { Shooter.atSetpoint() }
            ),
            AutoShoot(),
            DriveToPosition(AutoConstants.fourNoteFirstShotPose),
            IntakeNote(AllianceNote.STAGE),
            FinishIntakingThen(DriveToPosition(AutoConstants.centerNoteIntakePose)),
            AutoShoot(),
            IntakeNote(AllianceNote.CENTER),
            FinishIntakingThen(AlignToIntakeNote(AllianceNote.AMP)),
            AutoShoot(),
            IntakeNote(AllianceNote.AMP),
            FinishIntakingThen(DriveToPosition(AutoConstants.centerNoteIntakePose)),
            AutoShoot()
        )
    )
)