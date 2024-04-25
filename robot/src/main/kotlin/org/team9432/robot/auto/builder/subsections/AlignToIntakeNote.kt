package org.team9432.robot.auto.builder.subsections

import org.team9432.lib.commandbased.commands.SuppliedCommand
import org.team9432.robot.auto.AllianceNote
import org.team9432.robot.auto.AutoConstants
import org.team9432.robot.commands.drivetrain.DriveToPosition

/** Drives to the nearest intaking position for a given note. */
fun AlignToIntakeNote(note: AllianceNote) = SuppliedCommand {
    DriveToPosition(AutoConstants.getClosestIntakePosition(note))
}