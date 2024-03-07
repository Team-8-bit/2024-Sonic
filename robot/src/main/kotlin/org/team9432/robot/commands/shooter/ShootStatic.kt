package org.team9432.robot.commands.shooter

import edu.wpi.first.math.geometry.Rotation2d
import org.littletonrobotics.junction.Logger
import org.team9432.lib.commandbased.commands.*
import org.team9432.robot.FieldConstants
import org.team9432.robot.MechanismSide
import org.team9432.robot.RobotState
import org.team9432.robot.commands.drivetrain.TargetDrive
import org.team9432.robot.commands.hopper.MoveToSide
import org.team9432.robot.subsystems.RobotPosition
import org.team9432.robot.subsystems.ShooterInterpolator
import org.team9432.robot.subsystems.hood.CommandHood
import org.team9432.robot.subsystems.hopper.CommandHopper
import org.team9432.robot.subsystems.intake.CommandIntake
import org.team9432.robot.subsystems.shooter.CommandShooter

fun ShootStatic(rpmLeft: Double, rpmRight: Double) = SuppliedCommand {
    ParallelDeadlineCommand(
        TargetDrive { FieldConstants.speakerPose },

        CommandHood.followAngle {
            Rotation2d.fromDegrees(ShooterInterpolator.getHoodAngle(RobotPosition.distanceToSpeaker())).also {
                Logger.recordOutput("Hood/ShootTarget", it.degrees)
            }
        },

        CommandShooter.runSpeed { rpmLeft to rpmRight },

        deadline = SequentialCommand(
            ParallelCommand(
                // Move the note to the speaker side of the hopper
                MoveToSide(MechanismSide.SPEAKER),
                // Spin up the shooter for a minimum of half a second, plus one second per 4000 rpm
                WaitCommand(0.5 + (maxOf(rpmLeft, rpmRight) / 4000)),
            ),
            ParallelDeadlineCommand(
                // Shoot the note
                CommandHopper.runLoadTo(MechanismSide.SPEAKER, 5.0),
                CommandIntake.runIntakeSide(MechanismSide.SPEAKER, 5.0),
                // Do this for one second
                deadline = WaitCommand(1.0)
            ),

            // Update the note position
            InstantCommand { RobotState.notePosition = RobotState.NotePosition.NONE }
        )
    )
}
