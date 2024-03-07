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
    // Don't run this if there's not a note in the intake
    if (!RobotState.notePosition.isIntake) InstantCommand {}
    else {
        ParallelRaceCommand(
            TargetDrive { FieldConstants.speakerPose },

            CommandHood.followAngle {
                val angle = Rotation2d.fromDegrees(ShooterInterpolator.getHoodAngle(RobotPosition.distanceToSpeaker()))
                Logger.recordOutput("Hood/ShootTarget", angle.degrees)
                return@followAngle angle
            },

            SequentialCommand(
                ParallelCommand(
                    // Spin up the shooter
                    CommandShooter.setSpeed(rpmLeft, rpmRight),
                    // Move the note to the speaker side of the hopper
                    MoveToSide(MechanismSide.SPEAKER),
                    // Minimum of one second to spin up the shooter
                    WaitCommand(1.0),
                ),
                // Shoot the note
                CommandHopper.loadTo(MechanismSide.SPEAKER, 5.0),
                CommandIntake.intakeSide(MechanismSide.SPEAKER, 5.0),
                // Wait a second, then stop the motors
                WaitCommand(1.0),
                CommandShooter.stop(),
                CommandHopper.stop(),
                CommandIntake.stop(),
                // Update the note position
                InstantCommand { RobotState.notePosition = RobotState.NotePosition.NONE }
            )
        )
    }
}
