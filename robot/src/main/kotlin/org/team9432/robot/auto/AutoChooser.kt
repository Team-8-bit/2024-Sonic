package org.team9432.robot.auto

import org.littletonrobotics.junction.networktables.LoggedDashboardChooser
import org.team9432.lib.commandbased.KCommand

object AutoChooser {
    private val chooser = LoggedDashboardChooser<Auto>("Auto")

    init {
        chooser.addDefaultOption("Nothing", Auto.NOTHING)
        Auto.entries.forEach { chooser.addOption(it.displayName, it) }
    }

    fun getCommand(): KCommand {
        val auto = chooser.get()

        return if (auto == Auto.CUSTOM) AutoBuilder.getAuto() else auto.command.invoke()
    }
}