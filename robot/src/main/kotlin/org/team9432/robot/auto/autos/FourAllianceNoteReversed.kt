package org.team9432.robot.auto.autos

import edu.wpi.first.math.geometry.Rotation2d
import org.team9432.lib.commandbased.commands.ParallelCommand
import org.team9432.lib.commandbased.commands.SequentialCommand
import org.team9432.robot.auto.AllianceNote
import org.team9432.robot.auto.AutoConstants
import org.team9432.robot.auto.commands.*
import org.team9432.robot.auto.subsections.AlignToIntakeNote
import org.team9432.robot.auto.subsections.IntakeNote
import org.team9432.robot.commands.drivetrain.DriveToPosition

fun FourAllianceNoteReversed() = SequentialCommand(
    InitAuto(Rotation2d.fromDegrees(180.0)),
    ParallelCommand(
        CollectPreload(),
        DriveToPosition(AutoConstants.fourNoteFirstShotPoseReversed)
    ),
    AutoShoot(),
    IntakeNote(AllianceNote.AMP),
    FinishIntakingThen(DriveToPosition(AutoConstants.centerNoteIntakePose)),
    AutoShoot(),
    IntakeNote(AllianceNote.CENTER),
    FinishIntakingThen(AlignToIntakeNote(AllianceNote.STAGE)),
    AutoShoot(),
    IntakeNote(AllianceNote.STAGE),
    FinishIntakingThen(DriveToPosition(AutoConstants.centerNoteIntakePose)),
    AutoShoot(),
    ExitAuto(),
)