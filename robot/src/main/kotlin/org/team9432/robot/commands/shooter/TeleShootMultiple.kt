package org.team9432.robot.commands.shooter

import org.team9432.lib.commandbased.commands.*
import org.team9432.robot.FieldConstants
import org.team9432.robot.MechanismSide
import org.team9432.robot.RobotState
import org.team9432.robot.RobotState.NotePosition
import org.team9432.robot.commands.drivetrain.teleop.TeleTargetDrive
import org.team9432.robot.commands.hopper.MoveToPosition
import org.team9432.robot.commands.intake.TeleIntake
import org.team9432.robot.led.LEDState
import org.team9432.robot.oi.Controls
import org.team9432.robot.subsystems.Hood
import org.team9432.robot.subsystems.Shooter
import org.team9432.robot.subsystems.Superstructure

fun TeleShootMultiple() = ParallelCommand(
    Hood.Commands.aimAtSpeaker(),
    Shooter.Commands.runAtSpeeds(),

    RepeatCommand( // Just keep shooting :sunglasses:
        SuppliedCommand {
            if (RobotState.notePosition == NotePosition.NONE) {
                TeleIntake(endOnlyAfterNoteIsFullyCollected = true)
            } else {
                SequentialCommand(
                    // Aim, load the note, and wait until the shooter is sped up
                    ParallelCommand(
                        TeleTargetDrive(waitUntilAtSetpoint = true) { FieldConstants.speakerPose },
                        MoveToPosition(NotePosition.SPEAKER_HOPPER),
                        SequentialCommand(
                            WaitCommand(0.1), // We need to wait here to make sure it actually sets the setpoint
                            WaitUntilCommand { Shooter.atSetpoint() }
                        )
                    ),

                    InstantCommand { LEDState.speakerShooterReady = true },
                    WaitUntilCommand { Controls.readyToShootSpeaker }, // Wait for driver confirmation
                    InstantCommand { LEDState.speakerShooterReady = false },

                    ParallelDeadlineCommand(
                        // Shoot the note
                        Superstructure.Commands.runLoad(MechanismSide.SPEAKER),
                        // Do this for one second
                        deadline = WaitCommand(1.0)
                    ),

                    // Update the note position
                    InstantCommand { RobotState.notePosition = NotePosition.NONE }
                )
            }
        }
    )
)