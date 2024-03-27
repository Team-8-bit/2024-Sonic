package org.team9432.robot.commands.amp

import org.team9432.lib.commandbased.commands.*
import org.team9432.robot.MechanismSide
import org.team9432.robot.RobotState
import org.team9432.robot.commands.CommandConstants
import org.team9432.robot.commands.hopper.MoveToSide
import org.team9432.robot.led.LEDState
import org.team9432.robot.oi.Controls
import org.team9432.robot.subsystems.Amp
import org.team9432.robot.subsystems.Hopper
import org.team9432.robot.subsystems.Intake

fun ScoreAmp(volts: Double) = SequentialCommand(
    Amp.Commands.setVoltage(volts),

    ParallelCommand(
        // Move the note to the speaker side of the hopper
        MoveToSide(MechanismSide.AMP),
        WaitCommand(1.0),
    ),

    InstantCommand { LEDState.ampShooterReady = true },
    WaitUntilCommand { Controls.readyToShootAmp },
    InstantCommand { LEDState.ampShooterReady = false },

    ParallelDeadlineCommand(
        // Shoot the note
        Hopper.Commands.runLoadTo(MechanismSide.AMP, CommandConstants.HOPPER_SHOOT_SPEAKER_VOLTS),
        Intake.Commands.runIntakeSide(MechanismSide.AMP, CommandConstants.INTAKE_SHOOT_SPEAKER_VOLTS),
        // Do this for one second
        deadline = WaitCommand(1.0)
    ),
    Amp.Commands.stop(),
    // Update the note position
    InstantCommand { RobotState.notePosition = RobotState.NotePosition.NONE }
)