package org.team9432.robot.auto.subsections

import org.team9432.lib.commandbased.commands.*
import org.team9432.robot.MechanismSide
import org.team9432.robot.RobotState
import org.team9432.robot.auto.AllianceNote
import org.team9432.robot.auto.AutoConstants
import org.team9432.robot.commands.CommandConstants
import org.team9432.robot.commands.drivetrain.DriveSpeeds
import org.team9432.robot.commands.drivetrain.TargetAim
import org.team9432.robot.commands.hopper.MoveToSide
import org.team9432.robot.commands.intake.FinishIntakingAndAlign
import org.team9432.robot.subsystems.intake.CommandIntake
import org.team9432.robot.subsystems.intake.Intake

fun IntakeNote(note: AllianceNote) = SequentialCommand(
    // Move forwards until the note is touched
    ParallelDeadlineCommand(
        CommandIntake.runIntakeSide(MechanismSide.AMP, CommandConstants.INITIAL_INTAKE_VOLTS),

        SequentialCommand(
            TargetAim(MechanismSide.AMP) { AutoConstants.getNotePosition(note) },
            DriveSpeeds(vx = -1.0, fieldOriented = false),
        ),

        deadline = WaitUntilCommand { RobotState.noteInAmpSideIntakeBeambreak() }.withTimeout(2.0)
    )
)