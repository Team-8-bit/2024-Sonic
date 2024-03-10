package org.team9432.robot.commands.shooter

import org.team9432.lib.commandbased.commands.*
import org.team9432.robot.FieldConstants
import org.team9432.robot.MechanismSide
import org.team9432.robot.RobotState
import org.team9432.robot.commands.CommandConstants
import org.team9432.robot.commands.drivetrain.TargetDrive
import org.team9432.robot.commands.hood.HoodAimAtSpeaker
import org.team9432.robot.commands.hopper.MoveToSide
import org.team9432.robot.subsystems.hopper.CommandHopper
import org.team9432.robot.subsystems.intake.CommandIntake
import org.team9432.robot.subsystems.led.LEDState
import org.team9432.robot.subsystems.led.animations.ChargeUp
import org.team9432.robot.subsystems.led.animations.Rocket
import org.team9432.robot.subsystems.shooter.CommandShooter

fun PretendShoot(
    rpmLeft: Double = 4000.0,
    rpmRight: Double = 6000.0,
) = ParallelDeadlineCommand(
    TargetDrive { FieldConstants.speakerPose },

    InstantCommand { LEDState.animation = ChargeUp(1.0, 1.0) },

    deadline = SequentialCommand(
        WaitCommand(1.0),
        ParallelDeadlineCommand(
            SimpleCommand(
                isFinished = { !RobotState.noteInSpeakerSideHopperBeambreak() },
                end = { LEDState.animation = Rocket(0.5) }
            ),

            // Do this for one second
            deadline = WaitCommand(1.0)
        )
    )
)
