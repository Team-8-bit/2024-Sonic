package org.team9432.robot.commands.shooter

import edu.wpi.first.math.geometry.Rotation2d
import org.team9432.lib.commandbased.commands.*
import org.team9432.robot.MechanismSide
import org.team9432.robot.RobotState
import org.team9432.robot.commands.CommandConstants
import org.team9432.robot.commands.hopper.MoveToSide
import org.team9432.robot.led.LEDState
import org.team9432.robot.led.animations.ChargeUp
import org.team9432.robot.subsystems.Hood
import org.team9432.robot.subsystems.Hopper
import org.team9432.robot.subsystems.Intake
import org.team9432.robot.subsystems.Shooter

fun ShootAngle(rpmFast: Int, rpmSlow: Int, angle: Rotation2d) = ParallelDeadlineCommand(
    // Aim the hood and spin up the shooter
    Hood.Commands.followAngle { angle },

    InstantCommand { LEDState.animation = ChargeUp(1.0, 1.0) },

    deadline = SequentialCommand(
        Shooter.Commands.startRunAtSpeeds(rpmFast, rpmSlow),

        ParallelCommand(
            // Move the note to the speaker side of the hopper
            MoveToSide(MechanismSide.SPEAKER),
            WaitCommand(1.0),
        ),
        ParallelDeadlineCommand(
            // Shoot the note
            Hopper.Commands.runLoadTo(MechanismSide.SPEAKER, CommandConstants.HOPPER_SHOOT_SPEAKER_VOLTS),
            Intake.Commands.runIntakeSide(MechanismSide.SPEAKER, CommandConstants.INTAKE_SHOOT_SPEAKER_VOLTS),
            // Do this for one second
            deadline = WaitCommand(1.0)
        ),

        // Update the note position
        InstantCommand { RobotState.notePosition = RobotState.NotePosition.NONE },
        Shooter.Commands.stop()
    )
)
