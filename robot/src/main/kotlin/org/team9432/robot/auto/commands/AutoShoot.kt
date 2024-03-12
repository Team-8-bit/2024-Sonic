package org.team9432.robot.auto.commands

import org.team9432.lib.commandbased.commands.*
import org.team9432.robot.FieldConstants
import org.team9432.robot.MechanismSide
import org.team9432.robot.RobotState
import org.team9432.robot.commands.CommandConstants
import org.team9432.robot.commands.drivetrain.DriveSpeeds
import org.team9432.robot.commands.drivetrain.TargetAim
import org.team9432.robot.commands.hopper.MoveToSide
import org.team9432.robot.subsystems.beambreaks.BeambreakIOSim
import org.team9432.robot.subsystems.hopper.CommandHopper
import org.team9432.robot.subsystems.intake.CommandIntake
import org.team9432.robot.subsystems.led.LEDState
import org.team9432.robot.subsystems.led.animations.ChargeUp
import org.team9432.robot.subsystems.led.animations.Rocket

fun AutoShoot(driveCloser: Boolean) = ParallelDeadlineCommand(
    InstantCommand { RobotState.isUsingApriltags = false },
    InstantCommand { LEDState.animation = ChargeUp(1.0, 1.0) },

    deadline = SequentialCommand(
        ParallelCommand(
            TargetAim { FieldConstants.speakerPose },
            // Move the note to the speaker side of the hopper and drive forwards
            SuppliedCommand {
                if (driveCloser) {
                    ParallelRaceCommand(
                        DriveSpeeds(vx = 1.5, fieldOriented = false),
                        WaitCommand(0.75)
                    )
                } else InstantCommand {}
            }
        ),
        ParallelDeadlineCommand(
            // Shoot the note
            CommandHopper.runLoadTo(MechanismSide.SPEAKER, CommandConstants.HOPPER_SHOOT_SPEAKER_VOLTS),
            CommandIntake.runIntakeSide(MechanismSide.SPEAKER, CommandConstants.INTAKE_SHOOT_SPEAKER_VOLTS),

            deadline = WaitCommand(0.5)
        ),

        // Update the note position
        InstantCommand {
            RobotState.notePosition = RobotState.NotePosition.NONE
            RobotState.isUsingApriltags = true
            LEDState.animation = Rocket(0.5)
        }
    )
)
