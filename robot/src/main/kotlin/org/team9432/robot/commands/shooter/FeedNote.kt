package org.team9432.robot.commands.shooter

import edu.wpi.first.math.geometry.Rotation2d
import org.team9432.lib.commandbased.commands.*
import org.team9432.robot.LEDState
import org.team9432.robot.MechanismSide
import org.team9432.robot.RobotState
import org.team9432.robot.RobotState.NotePosition
import org.team9432.robot.commands.hopper.MoveToPosition
import org.team9432.robot.oi.Controls
import org.team9432.robot.oi.switches.DSSwitches
import org.team9432.robot.subsystems.Hood
import org.team9432.robot.subsystems.Shooter
import org.team9432.robot.subsystems.Superstructure

/** Runs the shooter and hood at the speeds required to feed a note over the stage. */
fun FeedNote() = ParallelDeadlineCommand(
    Hood.Commands.followAngle { Rotation2d.fromDegrees(25.0) },
    SuppliedCommand {
        when {
            DSSwitches.shouldFeedSlow -> Shooter.Commands.runAtSpeeds(4000.0, 2750.0)
            DSSwitches.shouldFeedFast -> Shooter.Commands.runAtSpeeds(5000.0, 3500.0)
            else -> Shooter.Commands.runAtSpeeds(4500.0, 3250.0)
        }
    },

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
        // waiting for confirmation
        WaitUntilCommand { Controls.readyToShootSpeaker }, // Wait for driver confirmation
        InstantCommand { LEDState.speakerShooterReady = false },

        ParallelDeadlineCommand(
            // Shoot the note
            Superstructure.Commands.runLoad(MechanismSide.SPEAKER),
            // Do this until the note is no longer in the beam break, plus a little bit
            deadline = SequentialCommand(
                WaitUntilCommand { !RobotState.noteInSpeakerSideHopperBeambreak() },
                WaitCommand(0.2)
            )
        ),

        // Update the note position
        InstantCommand { RobotState.notePosition = NotePosition.NONE },
    )
)