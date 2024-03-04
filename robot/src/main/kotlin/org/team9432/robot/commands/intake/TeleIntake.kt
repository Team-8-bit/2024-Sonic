package org.team9432.robot.commands.intake

import org.team9432.lib.commandbased.KCommand.InterruptionBehavior
import org.team9432.lib.commandbased.commands.*
import org.team9432.robot.RobotState
import org.team9432.robot.RobotState.NotePosition
import org.team9432.robot.subsystems.beambreaks.BeambreakIOSim
import org.team9432.robot.subsystems.beambreaks.Beambreaks
import org.team9432.robot.subsystems.intake.CommandIntake

fun TeleIntake() = SequentialCommand(
    // This part just gets the note touching the intake beam break
    CommandIntake.runTeleIntake(6.0),
    WaitUntilCommand { Beambreaks.getIntakeSpeakerSide() || Beambreaks.getIntakeAmpSide() },

    // Then it will finish collecting it at a slower speed and align the note
    // Instant command breaks off from the command group so letting go of the button doesn't interrupt the command in the middle of collecting/aligning a note
    InstantCommand {
        SequentialCommand(
            // Intake and align the note slowly with only the needed intake
            (if (Beambreaks.getIntakeAmpSide()) {
                SequentialCommand(
                    // Intake slowly until the note is fully in the intake
                    CommandIntake.intake(3.0, 0.0),
                    WaitUntilCommand { !Beambreaks.getIntakeAmpSide() }.afterSimDelay(0.25) { BeambreakIOSim.setNoteInIntakeAmpSide(false) },
                    // Push the note back into the intake beam break to leave more room before the hopper
                    CommandIntake.outtake(4.0, 0.0),
                    WaitUntilCommand { RobotState.noteInAmpSideIntakeBeambreak() }.afterSimDelay(0.25) { BeambreakIOSim.setNoteInIntakeAmpSide(true) },
                    // Update the note position in the robot
                    InstantCommand { RobotState.notePosition = NotePosition.AMP_INTAKE },
                )
            } else if (Beambreaks.getIntakeSpeakerSide()) {
                // Same as above but with the speaker intake
                SequentialCommand(
                    CommandIntake.intake(0.0, 3.0),
                    WaitUntilCommand { !Beambreaks.getIntakeSpeakerSide() }.afterSimDelay(0.25) { BeambreakIOSim.setNoteInIntakeSpeakerSide(false) },
                    CommandIntake.outtake(0.0, 4.0),
                    WaitUntilCommand { RobotState.noteInSpeakerSideIntakeBeambreak() }.afterSimDelay(0.25) { BeambreakIOSim.setNoteInIntakeSpeakerSide(true) },
                    InstantCommand { RobotState.notePosition = NotePosition.SPEAKER_INTAKE },
                )
            } else InstantCommand {}),
            // Always end by turning off the intake
            CommandIntake.stop()
        )
            .withTimeout(6.0) // Maximum time to finish intaking and align the note
            .withInterruptBehaviour(InterruptionBehavior.CANCEL_INCOMING) // Don't let this be interrupted
            .schedule()
    }
)
