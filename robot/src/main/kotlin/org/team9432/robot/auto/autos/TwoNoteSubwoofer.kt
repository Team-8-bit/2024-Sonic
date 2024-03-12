package org.team9432.robot.auto.autos

import edu.wpi.first.math.geometry.Rotation2d
import org.team9432.lib.commandbased.commands.*
import org.team9432.robot.MechanismSide
import org.team9432.robot.RobotState
import org.team9432.robot.auto.commands.AutoShoot
import org.team9432.robot.auto.commands.PullFromSpeakerShooter
import org.team9432.robot.commands.CommandConstants
import org.team9432.robot.commands.drivetrain.DriveSpeeds
import org.team9432.robot.commands.intake.FinishIntakingAndAlign
import org.team9432.robot.auto.commands.ShootFromHopper
import org.team9432.robot.subsystems.drivetrain.Drivetrain
import org.team9432.robot.subsystems.gyro.Gyro
import org.team9432.robot.subsystems.intake.CommandIntake

fun TwoNoteSubwoofer() = SequentialCommand(
    InstantCommand { RobotState.notePosition = RobotState.NotePosition.SPEAKER_HOPPER },
    InstantCommand { Gyro.setYaw(Rotation2d.fromDegrees(180.0)) },
    PullFromSpeakerShooter(),
    ShootFromHopper(),
    ParallelDeadlineCommand(
        // Drive to the position and then slowly move forwards
        DriveSpeeds(vx = 2.0 * Drivetrain.coordinateFlip),
        CommandIntake.runIntakeSide(MechanismSide.AMP, CommandConstants.INITIAL_INTAKE_VOLTS),

        deadline = WaitUntilCommand { RobotState.noteInAmpSideIntakeBeambreak() }.withTimeout(3.0)
    ),
    FinishIntakingAndAlign(),
    AutoShoot()
)