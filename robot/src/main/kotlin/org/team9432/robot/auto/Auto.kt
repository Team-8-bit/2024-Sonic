package org.team9432.robot.auto

import org.team9432.lib.commandbased.KCommand
import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.robot.auto.autos.FourAllianceNote
import org.team9432.robot.auto.autos.Preload
import org.team9432.robot.auto.autos.TopTwoCenterNote

enum class Auto(val displayName: String, val command: () -> KCommand) {
    CUSTOM("Custom", { throw Exception("This should be retrieved from the AutoBuilder ") }),
    ALLIANCE_FOUR_NOTE("Alliance Four Note", { FourAllianceNote() }),
    CENTER_TOP_TWO_NOTE("Top Two Center", { TopTwoCenterNote() }),
    NOTHING("Nothing", { InstantCommand {} }),
    PRELOAD("Preload", { Preload() }),
}