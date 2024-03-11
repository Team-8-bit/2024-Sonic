package org.team9432.robot.auto.subsections

import org.team9432.lib.commandbased.commands.SequentialCommand
import org.team9432.robot.auto.AllianceNote
import org.team9432.robot.auto.AutoShoot
import org.team9432.robot.auto.ShootFromHopper
import org.team9432.robot.commands.intake.FinishIntakingAndAlign

fun StartNote(note: AllianceNote) = SequentialCommand(
    // Drive to the position and then slowly move forwards
    AlignToIntakeNote(note),
    ShootFromHopper(),
    IntakeNote(note),
    FinishIntakingAndAlign(),
    AutoShoot()
)