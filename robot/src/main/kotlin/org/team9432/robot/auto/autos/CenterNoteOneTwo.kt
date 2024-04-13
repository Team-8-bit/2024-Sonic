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

fun CenterNoteOneTwo() = SequentialCommand(
    InitAuto(Rotation2d(Math.PI)),

    ParallelDeadlineCommand(
        Hood.Commands.aimAtSpeaker(),
        Shooter.Commands.runAtSpeeds(),

        deadline = SequentialCommand(
            // Shoot the preload
            ParallelCommand(
                PullFromSpeakerShooter(),
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
            AutoShoot()
        )
    )
)