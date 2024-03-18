package org.team9432.robot.subsystems.amp

import org.team9432.lib.commandbased.commands.InstantCommand

/* Interface for interacting with the subsystem through command based systems */
object CommandAmp {
    fun setVoltage(volts: Double) = InstantCommand(Amp) { Amp.setVoltage(volts) }
    fun stop() = InstantCommand(Amp) { Amp.stop() }
}