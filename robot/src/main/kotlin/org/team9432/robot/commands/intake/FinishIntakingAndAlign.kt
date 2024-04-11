package org.team9432.robot.commands.intake

import org.team9432.lib.commandbased.commands.*
import org.team9432.robot.MechanismSide
import org.team9432.robot.RobotState
import org.team9432.robot.commands.hopper.MoveToPosition
import org.team9432.robot.oi.Controls
import org.team9432.robot.oi.switches.DSSwitches
import org.team9432.robot.subsystems.Superstructure

fun FinishIntakingAndAlign() = SuppliedCommand(Superstructure) {
    val side = RobotState.getOneIntakeBeambreak() ?: return@SuppliedCommand InstantCommand {}

    SequentialCommand(
        // Intake slowly until the note is fully in the intake
        Superstructure.Commands.startIntakeSide(side, 6.0),
        WaitUntilCommand { !RobotState.noteInIntakeSide(side) },

        InstantCommand { Controls.setDriverRumble(1.0) },

        // Stop the intake
        Superstructure.Commands.stop(),

        // Update the note position in the robot
        InstantCommand { RobotState.notePosition = side.getNotePositionIntake() },

        // Move the note to the correct location based on our current primary scoring mode
        SuppliedCommand {
            when (DSSwitches.primaryScoringMechanism) {
                MechanismSide.AMP -> MoveToPosition(RobotState.NotePosition.SPEAKER_INTAKE)
                MechanismSide.SPEAKER -> MoveToPosition(RobotState.NotePosition.AMP_INTAKE)
            }
        },

        InstantCommand {
            SequentialCommand(
                WaitCommand(3.0),
                InstantCommand { Controls.setDriverRumble(0.0) }
            ).schedule()
        }
    )
}