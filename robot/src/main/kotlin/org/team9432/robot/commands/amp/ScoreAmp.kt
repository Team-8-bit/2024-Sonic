package org.team9432.robot.commands.amp

import org.team9432.lib.commandbased.commands.*
import org.team9432.robot.LEDState
import org.team9432.robot.MechanismSide
import org.team9432.robot.RobotState
import org.team9432.robot.RobotState.NotePosition
import org.team9432.robot.commands.hopper.MoveToPosition
import org.team9432.robot.oi.Controls
import org.team9432.robot.subsystems.Amp
import org.team9432.robot.subsystems.Superstructure

/** Scores in the amp, while spinning the rollers at the given voltage. */
fun ScoreAmp(volts: Double) = ParallelDeadlineCommand(
    Amp.Commands.runVoltage(volts),

    deadline = SequentialCommand(
        // Move the note to the speaker side of the hopper
        MoveToPosition(NotePosition.AMP_HOPPER),

        // Set the lights and wait for driver confirmation
        InstantCommand { LEDState.ampShooterReady = true },
        WaitUntilCommand { Controls.readyToShootAmp },
        InstantCommand { LEDState.ampShooterReady = false },

        ParallelDeadlineCommand(
            // Shoot the note
            Superstructure.Commands.runLoad(MechanismSide.AMP),
            // Do this until the note is no longer in the beam break, plus a little bit
            deadline = WaitCommand(1.0)
        ),
        // Update the note position
        InstantCommand { RobotState.notePosition = NotePosition.NONE }
    )
)