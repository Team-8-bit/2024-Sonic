package org.team9432.robot.subsystems.hopper

import org.littletonrobotics.junction.Logger
import org.team9432.Robot
import org.team9432.Robot.Mode.*
import org.team9432.lib.commandbased.KSubsystem
import org.team9432.robot.MechanismSide

object Hopper: KSubsystem() {
    private val io: HopperIO
    private val inputs = LoggedHopperIOInputs()

    init {
        io = when (Robot.mode) {
            REAL, REPLAY -> HopperIOReal()
            SIM -> HopperIOSim()
        }
    }

    override fun periodic() {
        io.updateInputs(inputs)
        Logger.processInputs("Hopper", inputs)
    }

    fun setVoltage(volts: Double) {
        io.setVoltage(volts)
    }

    fun loadTo(side: MechanismSide, volts: Double) =
        if (side == MechanismSide.SPEAKER) setVoltage(-volts) else setVoltage(volts)

    fun unloadFrom(side: MechanismSide, volts: Double) =
        if (side == MechanismSide.SPEAKER) setVoltage(volts) else setVoltage(-volts)

    fun stop() = io.stop()
}