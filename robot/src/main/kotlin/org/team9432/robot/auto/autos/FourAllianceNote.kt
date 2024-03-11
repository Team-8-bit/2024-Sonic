package org.team9432.robot.auto.autos

import edu.wpi.first.math.geometry.Rotation2d
import org.team9432.lib.commandbased.commands.SequentialCommand
import org.team9432.robot.auto.*
import org.team9432.robot.auto.subsections.AlignToIntakeNote
import org.team9432.robot.auto.subsections.IntakeNote

fun FourAllianceNote() = SequentialCommand(
    InitAuto(Rotation2d(Math.PI)),
    AlignToIntakeNote(AllianceNote.STAGE),
    ShootFromHopper(),
    IntakeNote(AllianceNote.STAGE),
    AlignToIntakeNote(AllianceNote.CENTER),
    AutoShoot(driveCloser = false),
    IntakeNote(AllianceNote.CENTER),
    AutoShoot(driveCloser = false),
    AlignToIntakeNote(AllianceNote.AMP),
    IntakeNote(AllianceNote.AMP),
    AutoShoot(driveCloser = true),
    ExitAuto(),
)