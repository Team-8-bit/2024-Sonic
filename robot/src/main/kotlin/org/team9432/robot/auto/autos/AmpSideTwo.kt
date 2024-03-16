package org.team9432.robot.auto.autos

import edu.wpi.first.math.geometry.Rotation2d
import org.team9432.lib.commandbased.commands.*
import org.team9432.robot.RobotState
import org.team9432.robot.auto.AllianceNote
import org.team9432.robot.auto.AutoConstants
import org.team9432.robot.auto.commands.*
import org.team9432.robot.auto.subsections.AlignToIntakeNote
import org.team9432.robot.auto.subsections.IntakeNote
import org.team9432.robot.commands.drivetrain.DriveToPosition

fun AmpSideTwo() = SequentialCommand(
    InitAuto(Rotation2d.fromDegrees(180.0)),
    ParallelCommand(
        CollectPreloadAndStartShooter(),
        DriveToPosition(AutoConstants.ampNoteAngledIntakePose)
    ),
    AutoShoot(),
    IntakeNote(AllianceNote.AMP),
    FinishIntakingThen(DriveToPosition(AutoConstants.ampNoteAngledIntakePose)),
    AutoShoot(),
    ExitAuto(),
)