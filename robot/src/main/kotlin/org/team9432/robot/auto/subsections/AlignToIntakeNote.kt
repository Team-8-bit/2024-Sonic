package org.team9432.robot.auto.subsections

import org.team9432.lib.commandbased.commands.SequentialCommand
import org.team9432.lib.commandbased.commands.SuppliedCommand
import org.team9432.robot.auto.AllianceNote
import org.team9432.robot.auto.AutoConstants
import org.team9432.robot.commands.drivetrain.DriveToPosition

fun AlignToIntakeNote(note: AllianceNote) = SuppliedCommand {
    // Drive to the position and then slowly move forwards
    DriveToPosition(AutoConstants.getIntakePosition(note))
}