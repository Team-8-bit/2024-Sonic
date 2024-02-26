package org.team9432.robot.commands

import org.team9432.lib.commandbased.commands.*
import org.team9432.robot.MechanismSide
import org.team9432.robot.RobotState
import org.team9432.robot.RobotState.NotePosition
import org.team9432.robot.subsystems.beambreaks.BeambreakIOSim
import org.team9432.robot.subsystems.hopper.CommandHopper
import org.team9432.robot.subsystems.intake.CommandIntake

fun MoveToSide(side: MechanismSide) = SuppliedCommand {
    if (RobotState.notePosition.isIntake) {
        SequentialCommand(
            CommandHopper.loadTo(side, volts = 10.0),

            // Wait a bit to get the hopper up to speed
            WaitCommand(0.5),

            // Both intakes need to be run when feeding across, but it could run only one when bending the note
            CommandIntake.setVoltage(-10.0, -10.0),

            // After the note is at the beam break, slowly unload and reload to align it
            WaitUntilCommand { RobotState.noteInHopperSide(side) }.afterSimDelay(1.0) {
                BeambreakIOSim.setNoteInHopper(
                    side,
                    true
                ); BeambreakIOSim.setNoteInIntakeAmpSide(false); BeambreakIOSim.setNoteInIntakeSpeakerSide(false)
            },

            // Unload
            CommandIntake.setVoltage(3.0, 3.0),
            CommandHopper.unloadFrom(side, volts = 3.0),
            WaitUntilCommand { !RobotState.noteInHopperSide(side) }.afterSimDelay(0.5) { BeambreakIOSim.setNoteInHopper(side, false) },

            // Reload
            CommandIntake.setVoltage(-3.0, -3.0),
            CommandHopper.loadTo(side, volts = 3.0),
            WaitUntilCommand { RobotState.noteInHopperSide(side) }.afterSimDelay(0.5) { BeambreakIOSim.setNoteInHopper(side, true) },

            ParallelCommand(CommandHopper.stop(), CommandIntake.stop()),
            InstantCommand {
                if (RobotState.noteInAmpSideHopperBeambreak()) RobotState.notePosition = NotePosition.AMP_HOPPER
                else if (RobotState.noteInSpeakerSideHopperBeambreak()) RobotState.notePosition = NotePosition.SPEAKER_HOPPER
            }
        )
    } else InstantCommand {}
}