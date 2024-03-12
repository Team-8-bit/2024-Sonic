package org.team9432.robot.auto.autos

import edu.wpi.first.math.geometry.Rotation2d
import org.team9432.lib.commandbased.commands.*
import org.team9432.robot.MechanismSide
import org.team9432.robot.auto.*
import org.team9432.robot.auto.commands.*
import org.team9432.robot.auto.subsections.AlignToIntakeNote
import org.team9432.robot.auto.subsections.IntakeNote
import org.team9432.robot.commands.drivetrain.DriveSpeeds
import org.team9432.robot.commands.hopper.MoveToSide
import org.team9432.robot.commands.intake.FinishIntakingAndAlign

fun FourAllianceNote() = SequentialCommand(
    InitAuto(Rotation2d(Math.PI)),
    ParallelCommand(
        CollectPreloadAndStartShooter(),
        AlignToIntakeNote(AllianceNote.STAGE)
    ),
    ShootFromHopper(),
    IntakeNote(AllianceNote.STAGE),
    ParallelCommand(
        SequentialCommand(
            WaitCommand(0.5),
            AlignToIntakeNote(AllianceNote.CENTER)
        ),
        SequentialCommand(
            MoveToSide(MechanismSide.SPEAKER),
            FinishIntakingAndAlign()
        )
    ),
    AutoShoot(driveCloser = false),
    IntakeNote(AllianceNote.CENTER),
    FinishIntakingAndAlign(),
    MoveToSide(MechanismSide.SPEAKER),
    AutoShoot(driveCloser = false),
    AlignToIntakeNote(AllianceNote.AMP),
    IntakeNote(AllianceNote.AMP),
    ParallelCommand(
        SequentialCommand(
            WaitCommand(0.5),
            ParallelDeadlineCommand(
                DriveSpeeds(vy = -3.0, fieldOriented = true),
                deadline = WaitCommand(0.5)
            )
        ),
        SequentialCommand(
            FinishIntakingAndAlign(),
            MoveToSide(MechanismSide.SPEAKER),
        )
    ),
    AutoShoot(driveCloser = false),
    ExitAuto(),
)