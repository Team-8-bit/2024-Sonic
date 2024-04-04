package org.team9432.robot.commands.shooter

import org.team9432.lib.commandbased.commands.*
import org.team9432.robot.MechanismSide
import org.team9432.robot.RobotState
import org.team9432.robot.commands.CommandConstants
import org.team9432.robot.commands.hopper.MoveToSide
import org.team9432.robot.led.LEDState
import org.team9432.robot.oi.Controls
import org.team9432.robot.subsystems.Hood
import org.team9432.robot.subsystems.Hopper
import org.team9432.robot.subsystems.Intake
import org.team9432.robot.subsystems.Shooter

fun TeleShoot() = ParallelDeadlineCommand(
    Hood.Commands.aimAtSpeaker(),

    deadline = SequentialCommand(
        Shooter.Commands.startRunAtSpeeds(),

        ParallelCommand(
            // Move the note to the speaker side of the hopper
            MoveToSide(MechanismSide.SPEAKER),
            WaitCommand(1.0),
        ),
        InstantCommand { LEDState.speakerShooterReady = true },
        WaitUntilCommand { Controls.readyToShootSpeaker },
        InstantCommand { LEDState.speakerShooterReady = false },
        ParallelDeadlineCommand(
            // Shoot the note
            Hopper.Commands.runLoadTo(MechanismSide.SPEAKER, CommandConstants.HOPPER_SHOOT_SPEAKER_VOLTS),
            Intake.Commands.runIntakeSide(MechanismSide.SPEAKER, CommandConstants.INTAKE_SHOOT_SPEAKER_VOLTS),

            SimpleCommand(
                isFinished = { !RobotState.noteInSpeakerSideHopperBeambreak() },
//                end = { LEDState.animation = Rocket(0.5) }
            ),

            // Do this for one second
            deadline = WaitCommand(1.0)
        ),

        // Update the note position
        InstantCommand { RobotState.notePosition = RobotState.NotePosition.NONE },

        Shooter.Commands.stop()
    )
)
