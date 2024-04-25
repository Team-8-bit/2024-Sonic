package org.team9432.robot.commands.hopper

import org.team9432.lib.commandbased.commands.*
import org.team9432.robot.MechanismSide
import org.team9432.robot.RobotState
import org.team9432.robot.subsystems.Superstructure

/** Move a note from an intake to a hopper. */
fun MoveFromIntakeToHopper(intakeSide: MechanismSide, hopperSide: MechanismSide) = SuppliedCommand {
    // If the note is going to a different side than the one it's already on
    val noteIsCrossing = intakeSide != hopperSide

    SequentialCommand(
        SuppliedCommand {
            when (hopperSide) {
                MechanismSide.AMP -> Superstructure.Commands.startHopperToLoadTo(hopperSide, 2.0)
                MechanismSide.SPEAKER -> Superstructure.Commands.startHopperToLoadTo(hopperSide, 1.5)
            }
        },

        // Let the hopper spin up a bit
        WaitCommand(0.25),

        // Both intakes need to be run when feeding across, but it runs only one when bending the note
        if (noteIsCrossing) Superstructure.Commands.startIntake(2.0, 2.0)
        else Superstructure.Commands.startIntakeSide(hopperSide, 2.0),

        // After the note is at the beam break, slowly unload to align it
        WaitUntilCommand { RobotState.noteInHopperSide(hopperSide) },

        // Unload the note until it is no longer blocking the beam break
        ParallelDeadlineCommand(
            Superstructure.Commands.runUnload(hopperSide, 2.0),
            deadline = WaitUntilCommand { !RobotState.noteInHopperSide(hopperSide) }
        ),

        // Update the note position in the robot
        InstantCommand { RobotState.notePosition = hopperSide.getNotePositionHopper() }
    )
}