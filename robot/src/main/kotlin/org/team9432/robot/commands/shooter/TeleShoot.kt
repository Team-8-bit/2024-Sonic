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

fun TeleShoot() = ParallelDeadlineCommand(
    Hood.Commands.aimAtSpeaker(),
    Shooter.Commands.runAtSpeeds(),
    TeleTargetDrive { FieldConstants.speakerPose },

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
        WaitUntilCommand { Controls.readyToShootSpeaker },
        InstantCommand { LEDState.speakerShooterReady = false },

        ParallelDeadlineCommand(
            // Shoot the note
            Superstructure.Commands.runLoad(MechanismSide.SPEAKER),
            // Do this for one second
            deadline = WaitCommand(1.0)
        ),

        // Update the note position
        InstantCommand { RobotState.notePosition = NotePosition.NONE },
    )
)