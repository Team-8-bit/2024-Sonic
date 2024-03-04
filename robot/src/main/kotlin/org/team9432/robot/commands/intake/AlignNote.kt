package org.team9432.robot.commands.intake

import org.team9432.lib.commandbased.commands.*
import org.team9432.robot.MechanismSide
import org.team9432.robot.RobotState
import org.team9432.robot.subsystems.beambreaks.BeambreakIOSim
import org.team9432.robot.subsystems.hopper.CommandHopper
import org.team9432.robot.subsystems.intake.CommandIntake
import org.team9432.robot.subsystems.intake.Intake

fun AlignNote() = SuppliedCommand(Intake) {
    when (RobotState.notePosition) {
        RobotState.NotePosition.AMP_INTAKE -> SequentialCommand(
            CommandIntake.runIntake(MechanismSide.AMP, -4.0),
//            CommandHopper.unloadFrom(MechanismSide.SPEAKER, 4.0),
            WaitUntilCommand { RobotState.noteInAmpSideIntakeBeambreak() }.afterSimDelay(0.5) {
                BeambreakIOSim.setNoteInIntakeAmpSide(true)
            },
            CommandIntake.stop(),
            CommandHopper.stop()
        )

        RobotState.NotePosition.SPEAKER_INTAKE -> SequentialCommand(
            CommandIntake.runIntake(MechanismSide.SPEAKER, -4.0),
//            CommandHopper.unloadFrom(MechanismSide.AMP, 4.0),
            WaitUntilCommand { RobotState.noteInSpeakerSideIntakeBeambreak() }.afterSimDelay(0.5) {
                BeambreakIOSim.setNoteInIntakeSpeakerSide(true)
            },
            CommandIntake.stop(),
            CommandHopper.stop()
        )

        else -> InstantCommand {}
    }
}