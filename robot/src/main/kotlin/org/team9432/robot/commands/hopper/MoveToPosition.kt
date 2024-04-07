package org.team9432.robot.commands.hopper

import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.lib.commandbased.commands.SuppliedCommand
import org.team9432.robot.RobotState

fun MoveToPosition(position: RobotState.NotePosition) = SuppliedCommand {
    InstantCommand {}
//    // If there's a note left in the hopper from auto, don't try to load it
//    if (RobotState.hasRemainingAutoNote) return@SuppliedCommand InstantCommand { RobotState.hasRemainingAutoNote = false }
//
//    // If the note is going to a different side than the one it's already on
//    val noteIsCrossing = RobotState.notePosition.side != side
//
//    SequentialCommand(
//        SuppliedCommand {
//            when (side) {
//                MechanismSide.AMP -> Superstructure.Commands.startLoadTo(side, 2.0)
//                MechanismSide.SPEAKER -> Superstructure.Commands.startLoadTo(side, 1.5)
//            }
//        },
//        // Let the hopper spin up a bit
//        WaitCommand(0.25),
//        // Both intakes need to be run when feeding across, but it runs only one when bending the note
//        if (noteIsCrossing) Superstructure.Commands.startIntake(2.0, 2.0)
//        else Superstructure.Commands.startIntakeSide(side, 2.0),
//        // After the note is at the beam break, slowly unload to align it
//        WaitUntilCommand { RobotState.noteInHopperSide(side) }
//            .afterSimDelay(1.0) {
//                BeambreakIOSim.setNoteInHopperSide(side, true)
//                BeambreakIOSim.setNoteInIntakeAmpSide(false)
//                BeambreakIOSim.setNoteInIntakeSpeakerSide(false)
//            },
//
//        ParallelDeadlineCommand(
//            // Unload the note until it is no longer blocking the beam break
//            Superstructure.Commands.runOuttakeSide(side, 2.0),
//            Superstructure.Commands.runUnloadFrom(side, 2.0),
//            deadline = WaitUntilCommand { !RobotState.noteInHopperSide(side) }.afterSimDelay(0.25) { BeambreakIOSim.setNoteInHopperSide(side, false) }
//        ),
//        // Update the note position in the robot
//        InstantCommand { RobotState.notePosition = side.getNotePositionHopper() }
//    )
}