package org.team9432.robot.auto.autos

import edu.wpi.first.math.geometry.Rotation2d
import org.team9432.lib.commandbased.commands.ParallelCommand
import org.team9432.lib.commandbased.commands.ParallelDeadlineCommand
import org.team9432.lib.commandbased.commands.SequentialCommand
import org.team9432.robot.auto.AutoConstants
import org.team9432.robot.auto.commands.AutoShoot
import org.team9432.robot.auto.commands.FinishIntakingThen
import org.team9432.robot.auto.commands.InitAuto
import org.team9432.robot.auto.commands.PullFromSpeakerShooter
import org.team9432.robot.auto.subsections.IntakeNote
import org.team9432.robot.commands.drivetrain.DriveToPosition
import org.team9432.robot.subsystems.Hood
import org.team9432.robot.subsystems.Shooter

fun CenterCenterNote() = SequentialCommand(
    InitAuto(Rotation2d.fromDegrees(-90.0)),

    ParallelDeadlineCommand(
        Hood.Commands.aimAtSpeaker(),
        Shooter.Commands.runAtSpeeds(),

        deadline = SequentialCommand(
            ParallelCommand(
                PullFromSpeakerShooter(),
                DriveToPosition(AutoConstants.centerCenterShot) // Shooting position
            ),
            AutoShoot(),
            DriveToPosition(AutoConstants.centerCenterDriveOne, positionalTolerance = 0.5),
            DriveToPosition(AutoConstants.centerStage),
            IntakeNote(AutoConstants.centerNoteThreeIntakePose, timeout = 0.5),
            FinishIntakingThen(DriveToPosition(AutoConstants.centerStage)),
            DriveToPosition(AutoConstants.centerCenterDriveOne, positionalTolerance = 0.5),
            DriveToPosition(AutoConstants.centerCenterShot),
            AutoShoot()
        )
    )
)