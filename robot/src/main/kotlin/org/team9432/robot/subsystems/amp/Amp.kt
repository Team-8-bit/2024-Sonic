package org.team9432.robot.subsystems.amp

import org.littletonrobotics.junction.Logger
import org.team9432.Robot
import org.team9432.Robot.Mode.*
import org.team9432.lib.commandbased.KSubsystem

object Amp: KSubsystem() {
    private val io: AmpIO
    private val inputs = LoggedAmpIOInputs()

    init {
        io = when (Robot.mode) {
            REAL, REPLAY -> AmpIONeo()
            SIM -> AmpIOSim()
        }
    }

    override fun periodic() {
        io.updateInputs(inputs)
        Logger.processInputs("Amp", inputs)
    }

    fun setVoltage(volts: Double) {
        io.setVoltage(volts)
    }

    fun stop() = io.stop()
}