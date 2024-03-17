package org.team9432.robot.subsystems.hopper

import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.lib.commandbased.commands.SimpleCommand
import org.team9432.robot.MechanismSide

/* Interface for interacting with the subsystem through command based systems */
object CommandHopper {
    fun setVoltage(volts: Double) = InstantCommand(Hopper) { Hopper.setVoltage(volts) }
    fun stop() = InstantCommand(Hopper) { Hopper.stop() }

    fun startLoadTo(side: MechanismSide, volts: Double) = InstantCommand(Hopper) { Hopper.loadTo(side, volts) }
    fun startUnloadFrom(side: MechanismSide, volts: Double) = InstantCommand(Hopper) { Hopper.unloadFrom(side, volts) }

    fun runLoadTo(side: MechanismSide, volts: Double) = SimpleCommand(
        requirements = setOf(Hopper),
        execute = { Hopper.loadTo(side, volts) },
        end = { Hopper.stop() }
    )

    fun runUnloadFrom(side: MechanismSide, volts: Double) = SimpleCommand(
        requirements = setOf(Hopper),
        execute = { Hopper.unloadFrom(side, volts) },
        end = { Hopper.stop() }
    )
}