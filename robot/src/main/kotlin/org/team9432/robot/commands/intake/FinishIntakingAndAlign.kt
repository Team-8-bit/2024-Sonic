package org.team9432.robot.commands.intake

import org.team9432.lib.commandbased.commands.*
import org.team9432.robot.oi.Controls
import org.team9432.robot.RobotState
import org.team9432.robot.sensors.beambreaks.BeambreakIOSim
import org.team9432.robot.subsystems.intake.CommandIntake
import org.team9432.robot.subsystems.intake.Intake

fun FinishIntakingAndAlign() = SuppliedCommand(Intake) {
    val side = RobotState.getOneIntakeBeambreak() ?: return@SuppliedCommand InstantCommand {}

    SequentialCommand(
        // Intake slowly until the note is fully in the intake
        CommandIntake.startIntakeSide(side, 6.0),
        WaitUntilCommand { !RobotState.noteInIntakeSide(side) }.afterSimDelay(0.25) { BeambreakIOSim.setNoteInIntakeSide(side, false) },
        // Stop the intake
        CommandIntake.stop(),
        // Update the note position in the robot
        InstantCommand { RobotState.notePosition = side.getNotePositionIntake() },

        InstantCommand {
            SequentialCommand(
                WaitCommand(3.0),
                InstantCommand { Controls.setDriverRumble(0.0) }
            ).schedule()
        }
    )
}