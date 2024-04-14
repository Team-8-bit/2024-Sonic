package org.team9432.robot.commands.shooter

import org.team9432.lib.commandbased.commands.*
import org.team9432.robot.FieldConstants
import org.team9432.robot.MechanismSide
import org.team9432.robot.RobotState
import org.team9432.robot.RobotState.NotePosition
import org.team9432.robot.commands.drivetrain.teleop.TeleTargetDrive
import org.team9432.robot.commands.hopper.MoveToPosition
import org.team9432.robot.led.LEDState
import org.team9432.robot.oi.Controls
import org.team9432.robot.subsystems.Hood
import org.team9432.robot.subsystems.Shooter
import org.team9432.robot.subsystems.Superstructure

fun Subwoofer() = ParallelDeadlineCommand(
    Shooter.Commands.runAtSubwooferSpeeds(),

    deadline = SequentialCommand(
        ParallelCommand(
            // Move the note to the speaker side of the hopper
            MoveToPosition(NotePosition.SPEAKER_HOPPER),
            SequentialCommand(
                WaitCommand(0.1),
                WaitUntilCommand { Shooter.atSetpoint() }
            )
        ),
        InstantCommand { LEDState.speakerShooterReady = true },
            // Keep aiming while waiting for confirmation
        WaitUntilCommand { Controls.readyToShootSpeaker }, // Wait for driver confirmation
        InstantCommand { LEDState.speakerShooterReady = false },

        ParallelDeadlineCommand(
            // Shoot the note
            Superstructure.Commands.runLoad(MechanismSide.SPEAKER),
            // Do this until the note is no longer in the beam break, plus a little bit
            deadline = WaitCommand(0.75)
        ),

        // Update the note position
        InstantCommand { RobotState.notePosition = NotePosition.NONE },
    )
)