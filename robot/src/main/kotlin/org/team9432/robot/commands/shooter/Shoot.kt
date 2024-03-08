package org.team9432.robot.commands.shooter

import org.team9432.lib.commandbased.commands.*
import org.team9432.robot.MechanismSide
import org.team9432.robot.RobotState
import org.team9432.robot.commands.CommandConstants
import org.team9432.robot.commands.hood.HoodAimAtSpeaker
import org.team9432.robot.commands.hopper.MoveToSide
import org.team9432.robot.subsystems.hopper.CommandHopper
import org.team9432.robot.subsystems.intake.CommandIntake
import org.team9432.robot.subsystems.shooter.CommandShooter

fun Shoot(
    rpmLeft: Double,
    rpmRight: Double,
    // Spin up the shooter for a minimum of half a second, plus one second per 4000 rpm
    minSpinup: Double = 0.5 + (maxOf(rpmLeft, rpmRight) / 4000),
) = ParallelDeadlineCommand(
    // Aim the hood and spin up the shooter
    HoodAimAtSpeaker(),
    CommandShooter.runSpeed { rpmLeft to rpmRight },

    deadline = SequentialCommand(
        ParallelCommand(
            // Move the note to the speaker side of the hopper
            MoveToSide(MechanismSide.SPEAKER),
            WaitCommand(minSpinup),
        ),
        ParallelDeadlineCommand(
            // Shoot the note
            CommandHopper.runLoadTo(MechanismSide.SPEAKER, CommandConstants.HOPPER_SHOOT_SPEAKER_VOLTS),
            CommandIntake.runIntakeSide(MechanismSide.SPEAKER, CommandConstants.INTAKE_SHOOT_SPEAKER_VOLTS),
            // Do this for one second
            deadline = WaitCommand(1.0)
        ),

        // Update the note position
        InstantCommand { RobotState.notePosition = RobotState.NotePosition.NONE }
    )
)
