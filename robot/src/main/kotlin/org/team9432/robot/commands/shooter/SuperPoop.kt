package org.team9432.robot.commands.shooter

import edu.wpi.first.math.geometry.Rotation2d
import org.team9432.lib.commandbased.commands.*
import org.team9432.robot.MechanismSide
import org.team9432.robot.RobotState
import org.team9432.robot.commands.hopper.MoveToSide
import org.team9432.robot.led.LEDState
import org.team9432.robot.oi.Controls
import org.team9432.robot.subsystems.Hood
import org.team9432.robot.subsystems.Shooter
import org.team9432.robot.subsystems.Superstructure

fun SuperPoop() = ParallelDeadlineCommand(
    Hood.Commands.followAngle { Rotation2d.fromDegrees(30.0) },
    Shooter.Commands.runAtFastSpeeds(),

    deadline = SequentialCommand(
        ParallelCommand(
            // Move the note to the speaker side of the hopper
            MoveToSide(MechanismSide.SPEAKER),
            WaitCommand(6.0)
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
        InstantCommand { RobotState.notePosition = RobotState.NotePosition.NONE },
    )
)