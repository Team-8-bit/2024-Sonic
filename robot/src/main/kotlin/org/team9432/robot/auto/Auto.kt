package org.team9432.robot.auto

import org.team9432.lib.commandbased.KCommand
import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.robot.auto.autos.*

enum class Auto(val displayName: String, val command: () -> KCommand) {
    CUSTOM("Custom", { throw Exception("This should be retrieved from the AutoBuilder ") }),
    ALLIANCE_FOUR_NOTE("Alliance Four Note", { FourAllianceNote() }),
    ALLIANCE_FOUR_NOTE_REVERSED("Reversed Alliance Four Note", { FourAllianceNoteReversed() }),
    CENTER_ONE_TWO("One Two Center", { CenterNoteOneTwo() }),
    CENTER_FIVE_FOUR("Five Four Center", { CenterNoteFiveFour() }),
    NOTHING("Nothing", { InstantCommand {} }),
    PRELOAD_AND_TAXI("Preload And Taxi", { PreloadAndTaxi() }),
    PRELOAD("Preload", { Preload() }),
    AMP_SIDE_TWO("Amp Side Two", { AmpSideTwo() })
}