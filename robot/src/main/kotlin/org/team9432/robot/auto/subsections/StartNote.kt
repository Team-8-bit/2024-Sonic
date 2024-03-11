package org.team9432.robot.auto.subsections

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import org.team9432.lib.commandbased.commands.*
import org.team9432.robot.MechanismSide
import org.team9432.robot.RobotState
import org.team9432.robot.auto.AutoShoot
import org.team9432.robot.commands.CommandConstants
import org.team9432.robot.commands.drivetrain.DriveSpeeds
import org.team9432.robot.commands.drivetrain.DriveSpeedsAndAim
import org.team9432.robot.commands.drivetrain.DriveToPosition
import org.team9432.robot.commands.intake.FinishIntakingAndAlign
import org.team9432.robot.commands.shooter.TeleShoot
import org.team9432.robot.auto.ShootFromHopper
import org.team9432.robot.commands.drivetrain.AngleAim
import org.team9432.robot.subsystems.drivetrain.Drivetrain
import org.team9432.robot.subsystems.intake.CommandIntake

fun StartNote(intakePosition: Pose2d) = SequentialCommand(
    DriveToPosition(intakePosition),
    ShootFromHopper(),
    ParallelDeadlineCommand(
        CommandIntake.runIntakeSide(MechanismSide.AMP, CommandConstants.INITIAL_INTAKE_VOLTS),

        SequentialCommand(
            AngleAim { Rotation2d.fromDegrees(Drivetrain.rotationOffset + 180.0) },
            // Drive to the position and then slowly move forwards
            DriveSpeedsAndAim({ Rotation2d.fromDegrees(Drivetrain.rotationOffset + 180.0) }, vx = 0.5 * Drivetrain.coordinateFlip),
        ),
        deadline = WaitUntilCommand { RobotState.noteInAmpSideIntakeBeambreak() }.withTimeout(1.5)
    ),
    FinishIntakingAndAlign(),
    AutoShoot()
)