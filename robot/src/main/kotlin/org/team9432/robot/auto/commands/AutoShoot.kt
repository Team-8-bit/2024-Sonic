package org.team9432.robot.auto.commands

import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.lib.commandbased.commands.ParallelDeadlineCommand
import org.team9432.lib.commandbased.commands.SequentialCommand
import org.team9432.lib.commandbased.commands.WaitCommand
import org.team9432.robot.FieldConstants
import org.team9432.robot.MechanismSide
import org.team9432.robot.RobotState
import org.team9432.robot.commands.CommandConstants
import org.team9432.robot.commands.drivetrain.TargetAim
import org.team9432.robot.led.LEDState
import org.team9432.robot.subsystems.Hopper
import org.team9432.robot.subsystems.Intake

fun AutoShoot() = ParallelDeadlineCommand(
   // InstantCommand { LEDState.animation = ChargeUp(0.5, 1.0) },

    deadline = SequentialCommand(
        TargetAim { FieldConstants.speakerPose },
        ParallelDeadlineCommand(
            // Shoot the note
            Hopper.Commands.runLoadTo(MechanismSide.SPEAKER, CommandConstants.HOPPER_SHOOT_SPEAKER_VOLTS),
            Intake.Commands.runIntakeSide(MechanismSide.SPEAKER, CommandConstants.INTAKE_SHOOT_SPEAKER_VOLTS),

            deadline = WaitCommand(0.5)
        ),

        // Update the note position
        InstantCommand {
            RobotState.notePosition = RobotState.NotePosition.NONE
//            LEDState.animation = Rocket(0.5)
        }
    )
)
