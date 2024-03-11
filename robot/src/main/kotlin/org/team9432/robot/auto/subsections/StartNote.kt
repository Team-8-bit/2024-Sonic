package org.team9432.robot.auto.subsections

import org.team9432.lib.commandbased.commands.ParallelDeadlineCommand
import org.team9432.lib.commandbased.commands.SequentialCommand
import org.team9432.lib.commandbased.commands.WaitUntilCommand
import org.team9432.lib.commandbased.commands.withTimeout
import org.team9432.robot.MechanismSide
import org.team9432.robot.RobotState
import org.team9432.robot.auto.AllianceNote
import org.team9432.robot.auto.AutoConstants
import org.team9432.robot.auto.AutoShoot
import org.team9432.robot.auto.ShootFromHopper
import org.team9432.robot.commands.CommandConstants
import org.team9432.robot.commands.drivetrain.DriveSpeeds
import org.team9432.robot.commands.drivetrain.DriveToPosition
import org.team9432.robot.commands.drivetrain.TargetAim
import org.team9432.robot.commands.intake.FinishIntakingAndAlign
import org.team9432.robot.subsystems.intake.CommandIntake

fun StartNote(note: AllianceNote) = SequentialCommand(
    // Drive to the position and then slowly move forwards
    DriveToPosition(AutoConstants.getIntakePosition(note)),
    ShootFromHopper(),
    ParallelDeadlineCommand(
        CommandIntake.runIntakeSide(MechanismSide.AMP, CommandConstants.INITIAL_INTAKE_VOLTS),

        SequentialCommand(
            TargetAim(MechanismSide.AMP) { AutoConstants.getNotePosition(note) },
            DriveSpeeds(vx = -0.5, fieldOriented = false),
        ),
        deadline = WaitUntilCommand { RobotState.noteInAmpSideIntakeBeambreak() }.withTimeout(1.5)
    ),
    FinishIntakingAndAlign(),
    AutoShoot()
)