package org.team9432.robot.commands.intake

import org.team9432.lib.commandbased.commands.*
import org.team9432.robot.MechanismSide
import org.team9432.robot.RobotState
import org.team9432.robot.subsystems.beambreaks.BeambreakIOSim
import org.team9432.robot.subsystems.intake.CommandIntake
import org.team9432.robot.subsystems.intake.Intake

fun AlignNote() = SuppliedCommand(Intake) {
    when (RobotState.notePosition) {
        RobotState.NotePosition.AMP_INTAKE -> SequentialCommand(
            CommandIntake.runIntake(MechanismSide.AMP, 4.0),
            WaitUntilCommand { !RobotState.noteInCenterBeambreak() }.afterSimDelay(0.5) { BeambreakIOSim.center = true },
            CommandIntake.runIntake(MechanismSide.AMP, -4.0),
            WaitUntilCommand { RobotState.noteInCenterBeambreak() }.afterSimDelay(0.5) { BeambreakIOSim.center = false },
            CommandIntake.stop()
        )

        RobotState.NotePosition.SPEAKER_INTAKE -> SequentialCommand(
            CommandIntake.runIntake(MechanismSide.SPEAKER, 4.0),
            WaitUntilCommand { !RobotState.noteInCenterBeambreak() }.afterSimDelay(0.5) { BeambreakIOSim.center = true },
            CommandIntake.runIntake(MechanismSide.SPEAKER, -4.0),
            WaitUntilCommand { RobotState.noteInCenterBeambreak() }.afterSimDelay(0.5) { BeambreakIOSim.center = false },
            CommandIntake.stop()
        )

        else -> InstantCommand {}
    }
}