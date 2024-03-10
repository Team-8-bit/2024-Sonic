package org.team9432.robot.auto.subsections

import edu.wpi.first.math.geometry.Rotation2d
import org.team9432.lib.commandbased.commands.*
import org.team9432.robot.RobotState
import org.team9432.robot.auto.AutoConstants
import org.team9432.robot.commands.drivetrain.DriveSpeedsAndAim
import org.team9432.robot.commands.drivetrain.DriveToPosition
import org.team9432.robot.commands.shooter.PretendShoot
import org.team9432.robot.subsystems.drivetrain.Drivetrain

fun StartCenterNote() = SequentialCommand(
    DriveToPosition(AutoConstants.centerNoteIntakePose),
    PretendShoot(),
    ParallelDeadlineCommand(
        // Drive to the position and then slowly move forwards
        DriveSpeedsAndAim({ Rotation2d(0.0) }, vx = 0.5 * Drivetrain.coordinateFlip),

        deadline = WaitUntilCommand { RobotState.noteInAmpSideIntakeBeambreak() }.withTimeout(1.0)
    ),
    PretendShoot()
)