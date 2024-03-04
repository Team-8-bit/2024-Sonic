package org.team9432.robot.commands.intake

import org.team9432.lib.commandbased.KCommand.InterruptionBehavior
import org.team9432.lib.commandbased.commands.*
import org.team9432.robot.RobotState
import org.team9432.robot.subsystems.beambreaks.BeambreakIOSim
import org.team9432.robot.subsystems.intake.CommandIntake
import org.team9432.robot.subsystems.intake.Intake

fun TeleIntake() = SequentialCommand(
    // This part just gets the note touching the first intake beam break
    CommandIntake.runTeleIntake(6.0),
    WaitUntilCommand { RobotState.noteInAnyIntake() },

    // Then it will finish collecting it at a slower speed and align the note
    // Instant command breaks off from the command group so letting go of the button doesn't interrupt the command in the middle of collecting/aligning a note
    InstantCommand {
        SuppliedCommand(Intake) {
            val side = RobotState.getOneIntakeBeambreak() ?: return@SuppliedCommand InstantCommand {}

            SequentialCommand(
                // Intake slowly until the note is fully in the intake
                CommandIntake.intakeSide(side, 3.0),
                WaitUntilCommand { !RobotState.noteInIntakeSide(side) }.afterSimDelay(0.25) { BeambreakIOSim.setNoteInIntakeSide(side, false) },
                // Push the note back into the intake beam break to leave more room before the hopper
                CommandIntake.outtakeSide(side, 3.0),
                WaitUntilCommand { RobotState.noteInIntakeSide(side) }.afterSimDelay(0.25) { BeambreakIOSim.setNoteInIntakeSide(side, true) },
                // Stop the intake
                CommandIntake.stop(),
                // Update the note position in the robot
                InstantCommand { RobotState.notePosition = side.getNotePositionIntake() }
            )
        }
            .withTimeout(6.0) // Maximum time to finish intaking and align the note
            .withInterruptBehaviour(InterruptionBehavior.CANCEL_INCOMING) // Don't let this be interrupted
            .schedule()
    }
)
