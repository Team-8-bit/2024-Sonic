package org.team9432.robot.commands.shooter

import org.team9432.lib.commandbased.commands.*
import org.team9432.robot.FieldConstants
import org.team9432.robot.MechanismSide
import org.team9432.robot.RobotState
import org.team9432.robot.commands.CommandConstants
import org.team9432.robot.commands.drivetrain.TargetDrive
import org.team9432.robot.commands.hopper.MoveToSide
import org.team9432.robot.subsystems.RobotPosition
import org.team9432.robot.subsystems.hopper.CommandHopper
import org.team9432.robot.subsystems.intake.CommandIntake
import org.team9432.robot.subsystems.led.LEDState
import org.team9432.robot.subsystems.led.animations.ChargeUp
import org.team9432.robot.subsystems.led.animations.Rocket
import org.team9432.robot.subsystems.shooter.CommandShooter

fun TeleShoot() = ParallelDeadlineCommand(
    InstantCommand { RobotState.isUsingApriltags = false },

    TargetDrive { FieldConstants.speakerPose },

    CommandShooter.startRunAtSpeeds(),

    InstantCommand { LEDState.animation = ChargeUp(1.0, 1.0) },

    deadline = SequentialCommand(
        ParallelCommand(
            // Move the note to the speaker side of the hopper
            MoveToSide(MechanismSide.SPEAKER),
            WaitCommand(1.0),
        ),
        ParallelDeadlineCommand(
            // Shoot the note
            CommandHopper.runLoadTo(MechanismSide.SPEAKER, CommandConstants.HOPPER_SHOOT_SPEAKER_VOLTS),
            CommandIntake.runIntakeSide(MechanismSide.SPEAKER, CommandConstants.INTAKE_SHOOT_SPEAKER_VOLTS),

            SimpleCommand(
                isFinished = { !RobotState.noteInSpeakerSideHopperBeambreak() },
                end = { LEDState.animation = Rocket(0.5) }
            ),

            // Do this for one second
            deadline = WaitCommand(1.0)
        ),

        // Update the note position
        InstantCommand { RobotState.notePosition = RobotState.NotePosition.NONE },
        InstantCommand { RobotState.isUsingApriltags = true }
    )
)
