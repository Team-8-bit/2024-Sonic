package org.team9432.robot.auto

import org.team9432.lib.commandbased.commands.*
import org.team9432.robot.FieldConstants
import org.team9432.robot.MechanismSide
import org.team9432.robot.RobotState
import org.team9432.robot.commands.CommandConstants
import org.team9432.robot.commands.drivetrain.DriveSpeeds
import org.team9432.robot.commands.drivetrain.DriveTargetSpeeds
import org.team9432.robot.commands.drivetrain.TargetAim
import org.team9432.robot.commands.drivetrain.TargetDrive
import org.team9432.robot.commands.hopper.MoveToSide
import org.team9432.robot.subsystems.hopper.CommandHopper
import org.team9432.robot.subsystems.intake.CommandIntake
import org.team9432.robot.subsystems.led.LEDState
import org.team9432.robot.subsystems.led.animations.ChargeUp
import org.team9432.robot.subsystems.led.animations.Rocket
import org.team9432.robot.subsystems.shooter.CommandShooter

fun AutoShoot() = ParallelDeadlineCommand(
    InstantCommand { RobotState.isUsingApriltags = false },

    InstantCommand { LEDState.animation = ChargeUp(1.0, 1.0) },

    deadline = SequentialCommand(
        ParallelCommand(
            // Move the note to the speaker side of the hopper and drive forwards
            MoveToSide(MechanismSide.SPEAKER),
            ParallelRaceCommand(
                DriveSpeeds(vx = 1.0, fieldOriented = false),
                WaitCommand(1.0)
            )
        ),
        TargetAim { FieldConstants.speakerPose },
        ParallelDeadlineCommand(
            // Shoot the note
            CommandHopper.runLoadTo(MechanismSide.SPEAKER, CommandConstants.HOPPER_SHOOT_SPEAKER_VOLTS),
            CommandIntake.runIntakeSide(MechanismSide.SPEAKER, CommandConstants.INTAKE_SHOOT_SPEAKER_VOLTS),

            SimpleCommand(
                isFinished = { !RobotState.noteInSpeakerSideHopperBeambreak() },
                end = { LEDState.animation = Rocket(0.5) }
            ),

            // Do this for one second
            deadline = WaitCommand(0.5)
        ),

        // Update the note position
        InstantCommand { RobotState.notePosition = RobotState.NotePosition.NONE },
        InstantCommand { RobotState.isUsingApriltags = true }
    )
)
