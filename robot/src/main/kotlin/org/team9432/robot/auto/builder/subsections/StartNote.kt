package org.team9432.robot.auto.builder.subsections

import org.team9432.lib.commandbased.commands.SequentialCommand
import org.team9432.robot.auto.SpikeNote
import org.team9432.robot.auto.autos.FinishIntakingAndLoadToSpeaker
import org.team9432.robot.commands.shooter.AutoShoot

/** Drives to the note, shoots the preload, collects the second note, and shoots from that spot. */
fun StartNote(note: SpikeNote) = SequentialCommand(
    AlignToIntakeNote(note),
    AutoShoot(),
    IntakeNote(note),
    FinishIntakingAndLoadToSpeaker(),
    AutoShoot()
)