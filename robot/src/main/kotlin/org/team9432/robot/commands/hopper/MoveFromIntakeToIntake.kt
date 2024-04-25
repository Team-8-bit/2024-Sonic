package org.team9432.robot.commands.hopper

import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.lib.commandbased.commands.ParallelDeadlineCommand
import org.team9432.lib.commandbased.commands.SequentialCommand
import org.team9432.lib.commandbased.commands.WaitUntilCommand
import org.team9432.robot.MechanismSide
import org.team9432.robot.RobotState
import org.team9432.robot.subsystems.Superstructure

/** Move a note from an intake to an intake. */
fun MoveFromIntakeToIntake(initialSide: MechanismSide, targetSide: MechanismSide) = SequentialCommand(
    // Unload from the hopper
    ParallelDeadlineCommand(
        Superstructure.Commands.runIntakeToIntake(initialSide, targetSide),
        deadline = WaitUntilCommand { RobotState.noteInIntakeSide(targetSide) },
    ),

    // Intake the note until it is no longer blocking the beam break
    ParallelDeadlineCommand(
        Superstructure.Commands.runIntakeSide(targetSide),
        deadline = WaitUntilCommand { !RobotState.noteInIntakeSide(targetSide) }
    ),

    // Update the note position in the robot
    InstantCommand { RobotState.notePosition = targetSide.getNotePositionIntake() }
)
