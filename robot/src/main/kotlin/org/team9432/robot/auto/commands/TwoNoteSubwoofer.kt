package org.team9432.robot.auto.commands

import org.team9432.lib.commandbased.commands.*
import org.team9432.robot.MechanismSide
import org.team9432.robot.RobotState
import org.team9432.robot.auto.AutoConstants
import org.team9432.robot.commands.CommandConstants
import org.team9432.robot.commands.drivetrain.DriveSpeeds
import org.team9432.robot.commands.drivetrain.DriveToPosition
import org.team9432.robot.commands.intake.FinishIntakingAndAlign
import org.team9432.robot.commands.shooter.Shoot
import org.team9432.robot.subsystems.intake.CommandIntake

fun TwoNoteSubwoofer() = SequentialCommand(
    Shoot(4000.0, 6000.0),
    ParallelDeadlineCommand(
        // Drive to the position and then slowly move forwards
        SequentialCommand(
            DriveToPosition(AutoConstants.podiumNoteIntakePose),
            DriveSpeeds(vx = 0.5)
        ),
        CommandIntake.runIntakeSide(MechanismSide.AMP, CommandConstants.INITIAL_INTAKE_VOLTS),

        deadline = WaitUntilCommand { RobotState.noteInAmpSideIntakeBeambreak() }.withTimeout(3.0)
    ),
    ParallelCommand(
        FinishIntakingAndAlign(),
        DriveToPosition(AutoConstants.directScoringPose)
    ),
    Shoot(4000.0, 6000.0)
)