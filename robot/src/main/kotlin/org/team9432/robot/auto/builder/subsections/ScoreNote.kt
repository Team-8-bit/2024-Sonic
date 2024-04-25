package org.team9432.robot.auto.builder.subsections

import org.team9432.lib.commandbased.commands.SequentialCommand
import org.team9432.robot.auto.SpikeNote
import org.team9432.robot.auto.autos.FinishIntakingAndLoadToSpeaker
import org.team9432.robot.commands.shooter.AutoShoot

/** Drives to the note, collects it, and shoots from that spot. */
fun ScoreNote(note: SpikeNote) = SequentialCommand(
    AlignToIntakeNote(note),
    IntakeNote(note),
    FinishIntakingAndLoadToSpeaker(),
    AutoShoot()
)