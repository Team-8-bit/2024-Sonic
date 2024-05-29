package org.team9432.robot.auto

import org.littletonrobotics.junction.networktables.LoggedDashboardChooser
import org.team9432.DashboardTab
import org.team9432.dashboard.lib.widgets.DashboardDropdown
import org.team9432.lib.commandbased.KCommand
import org.team9432.robot.auto.builder.AutoBuilder

object AutoChooser {
    private val autoMap = Auto.entries.associateBy { it.displayName }
    private val chooser = LoggedDashboardChooser<Auto>("Auto")
    private val newChooser = DashboardDropdown("Auto", autoMap.keys.toList(), row = 0, col = 2, DashboardTab.COMPETITION, colsSpanned = 2) {}

    fun initChooser() {
        chooser.addDefaultOption("Nothing", Auto.NOTHING)
        Auto.entries.forEach { chooser.addOption(it.displayName, it) }
    }

    /** Get the currently selected auto command from shuffleboard. */
    fun getCommand(): KCommand {
        val newAuto = autoMap[newChooser.getCurrentOption()]
        val oldAuto = chooser.get()
        val auto = newAuto ?: oldAuto ?: Auto.NOTHING

        // Get custom auto from the builder
        return if (auto == Auto.CUSTOM) AutoBuilder.getAuto() else auto.command()
    }
}