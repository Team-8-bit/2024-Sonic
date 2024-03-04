package org.team9432.robot.commands.hopper

import org.team9432.lib.commandbased.KCommand
import org.team9432.lib.commandbased.commands.*
import org.team9432.robot.MechanismSide
import org.team9432.robot.RobotState
import org.team9432.robot.subsystems.beambreaks.BeambreakIOSim
import org.team9432.robot.subsystems.hopper.CommandHopper
import org.team9432.robot.subsystems.intake.CommandIntake

fun MoveToSide(side: MechanismSide): KCommand {
    // This shouldn't happen if there isn't a note in the intake
    return if (!RobotState.notePosition.isIntake) InstantCommand {}
    else {
        // If the note is going to a different side than the one it's already on
        val noteIsCrossing = RobotState.notePosition.side != side

        SequentialCommand(
            CommandHopper.loadTo(side, 4.0),
            // Let the hopper spin up a bit
            WaitCommand(0.125),
            // Both intakes need to be run when feeding across, but it runs only one when bending the note
            if (noteIsCrossing) CommandIntake.intake(4.0, 4.0)
            else CommandIntake.intakeSide(side, 4.0),
            // After the note is at the beam break, slowly unload to align it
            WaitUntilCommand { RobotState.noteInHopperSide(side) }
                .afterSimDelay(1.0) {
                    BeambreakIOSim.setNoteInHopperSide(side, true)
                    BeambreakIOSim.setNoteInIntakeAmpSide(false)
                    BeambreakIOSim.setNoteInIntakeSpeakerSide(false)
                },
            // Unload the note until it is no longer blocking the beam break
            CommandIntake.outtakeSide(side, 2.0),
            CommandHopper.unloadFrom(side, 2.0),
            WaitUntilCommand { !RobotState.noteInHopperSide(side) }.afterSimDelay(0.25) { BeambreakIOSim.setNoteInHopperSide(side, false) },
            // Stop everything
            CommandHopper.stop(),
            CommandIntake.stop(),
            // Update the note position in the robot
            InstantCommand { RobotState.notePosition = side.getNotePositionHopper() }
        )
    }
}