package org.team9432.robot.commands.amp

import org.team9432.lib.commandbased.commands.*
import org.team9432.robot.MechanismSide
import org.team9432.robot.RobotState
import org.team9432.robot.commands.CommandConstants
import org.team9432.robot.commands.hopper.MoveToSide
import org.team9432.robot.subsystems.amp.CommandAmp
import org.team9432.robot.subsystems.hopper.CommandHopper
import org.team9432.robot.subsystems.intake.CommandIntake

fun ScoreAmp(volts: Double) = SequentialCommand(
    CommandAmp.setVoltage(volts),

    ParallelCommand(
        // Move the note to the speaker side of the hopper
        MoveToSide(MechanismSide.AMP),
        WaitCommand(1.0),
    ),
    ParallelDeadlineCommand(
        // Shoot the note
        CommandHopper.runLoadTo(MechanismSide.AMP, CommandConstants.HOPPER_SHOOT_SPEAKER_VOLTS),
        CommandIntake.runIntakeSide(MechanismSide.AMP, CommandConstants.INTAKE_SHOOT_SPEAKER_VOLTS),
        // Do this for one second
        deadline = WaitCommand(1.0)
    ),
    CommandAmp.stop(),
    // Update the note position
    InstantCommand { RobotState.notePosition = RobotState.NotePosition.NONE }
)