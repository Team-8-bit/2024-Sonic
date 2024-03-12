package org.team9432.robot.auto

import org.team9432.lib.commandbased.KCommand
import org.team9432.lib.commandbased.commands.InstantCommand

enum class Auto(val displayName: String, val command: () -> KCommand) {
    CUSTOM("Custom", { throw Exception("This should be retrieved from the AutoBuilder ") }),
    ALLIANCE_FOUR_NOTE("Alliance_Four_Note", { org.team9432.robot.auto.autos.FourAllianceNote() }),
    NOTHING("Nothing", { InstantCommand {} });
}