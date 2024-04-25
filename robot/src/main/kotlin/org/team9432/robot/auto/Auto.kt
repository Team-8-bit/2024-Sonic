package org.team9432.robot.auto

import org.team9432.lib.commandbased.KCommand
import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.robot.auto.autos.*

// All auto options, anything added here will be sent to shuffleboard as an option
enum class Auto(val displayName: String, val command: () -> KCommand) {
    CUSTOM("Custom", { throw Exception("This should be retrieved from the AutoBuilder ") }),
    FOUR_SPIKE_NOTE("Four Spike Note", { FourAllianceNote() }),
    FOUR_SPIKE_NOTE_REVERSED("Reversed Four Spike Note", { FourAllianceNoteReversed() }),
    CENTER_CENTER("CenterCenter", { CenterCenterNote() }),
    NOTHING("Nothing", { InstantCommand {} }),
    PRELOAD_AND_TAXI("Preload And Taxi", { PreloadAndTaxi() }),
    PRELOAD("Preload", { Preload() }),
}