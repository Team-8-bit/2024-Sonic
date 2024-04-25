package org.team9432.robot.commands.hopper

import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.lib.commandbased.commands.ParallelDeadlineCommand
import org.team9432.lib.commandbased.commands.SequentialCommand
import org.team9432.lib.commandbased.commands.WaitUntilCommand
import org.team9432.robot.MechanismSide
import org.team9432.robot.RobotState
import org.team9432.robot.subsystems.Superstructure

/** Move a note from a hopper to an intake. */
fun MoveFromHopperToIntake(hopperSide: MechanismSide, intakeSide: MechanismSide) = SequentialCommand(
    // Unload from the hopper
    ParallelDeadlineCommand(
        Superstructure.Commands.runHopperToIntake(hopperSide),
        deadline = WaitUntilCommand { RobotState.noteInIntakeSide(intakeSide) },
    ),

    // Intake the note until it is no longer blocking the beam break
    ParallelDeadlineCommand(
        Superstructure.Commands.runIntakeSide(intakeSide),
        deadline = WaitUntilCommand { !RobotState.noteInIntakeSide(intakeSide) }
    ),

    // Update the note position in the robot
    InstantCommand { RobotState.notePosition = intakeSide.getNotePositionIntake() }
)
