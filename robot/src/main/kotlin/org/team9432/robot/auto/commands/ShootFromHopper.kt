package org.team9432.robot.auto.commands

import org.team9432.lib.commandbased.commands.*
import org.team9432.robot.FieldConstants
import org.team9432.robot.MechanismSide
import org.team9432.robot.RobotState
import org.team9432.robot.commands.CommandConstants
import org.team9432.robot.commands.drivetrain.TargetAim
import org.team9432.robot.commands.drivetrain.TargetDrive
import org.team9432.robot.subsystems.hopper.CommandHopper
import org.team9432.robot.subsystems.intake.CommandIntake
import org.team9432.robot.subsystems.led.LEDState
import org.team9432.robot.subsystems.led.animations.ChargeUp
import org.team9432.robot.subsystems.shooter.CommandShooter

fun ShootFromHopper() = ParallelDeadlineCommand(
    InstantCommand { RobotState.isUsingApriltags = false },

    InstantCommand { LEDState.animation = ChargeUp(1.0, 1.0) },

    deadline = SequentialCommand(
        // Aim the hood and spin up the shooter
        TargetAim { FieldConstants.speakerPose },

        ParallelDeadlineCommand(
            // Shoot the note
            CommandHopper.runLoadTo(MechanismSide.SPEAKER, CommandConstants.HOPPER_SHOOT_SPEAKER_VOLTS),
            CommandIntake.runIntakeSide(MechanismSide.SPEAKER, CommandConstants.INTAKE_SHOOT_SPEAKER_VOLTS),
            // Do this for one second
            deadline = WaitCommand(0.5)
        ),

        // Update the note position
        InstantCommand { RobotState.notePosition = RobotState.NotePosition.NONE },
        InstantCommand { RobotState.isUsingApriltags = true },
    )
)
