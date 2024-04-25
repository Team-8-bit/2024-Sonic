package org.team9432.robot.auto

import org.littletonrobotics.junction.networktables.LoggedDashboardChooser
import org.team9432.lib.commandbased.KCommand
import org.team9432.robot.auto.builder.AutoBuilder

object AutoChooser {
    private val chooser = LoggedDashboardChooser<Auto>("Auto")

    fun initChooser() {
        chooser.addDefaultOption("Nothing", Auto.NOTHING)
        Auto.entries.forEach { chooser.addOption(it.displayName, it) }
    }

    /** Get the currently selected auto command from shuffleboard. */
    fun getCommand(): KCommand {
        val auto = chooser.get() ?: Auto.NOTHING

        // Get custom auto from the builder
        return if (auto == Auto.CUSTOM) AutoBuilder.getAuto() else auto.command()
    }
}