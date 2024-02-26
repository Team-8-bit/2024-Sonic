package org.team9432.robot.subsystems.hopper

import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.robot.MechanismSide

/* Interface for interacting with the subsystem through command based systems */
object CommandHopper {
    fun setVoltage(volts: Double) = InstantCommand(Hopper) { Hopper.setVoltage(volts) }
    fun stop() = InstantCommand(Hopper) { Hopper.stop() }
    fun setSpeed(rotationsPerMinute: Double) = InstantCommand(Hopper) { Hopper.setSpeed(rotationsPerMinute) }

    fun loadTo(side: MechanismSide, volts: Double) = InstantCommand(Hopper) { Hopper.loadTo(side, volts) }
    fun unloadFrom(side: MechanismSide, volts: Double) = InstantCommand(Hopper) { Hopper.unloadFrom(side, volts) }
}