package org.team9432.lib.motors.neo

import org.littletonrobotics.junction.Logger
import org.team9432.Robot
import org.team9432.Robot.Mode.*
import org.team9432.lib.commandbased.KPeriodic
import org.team9432.lib.wrappers.SparkMax

class NEO(config: Config, private val logName: String, simGearRatio: Double, simJkgMetersSquared: Double): KPeriodic() {
    private val io: NeoIO
    private val inputs = LoggedNEOIOInputs()

    init {
        io = when (Robot.mode) {
            REAL, REPLAY -> NeoIONeo(config)
            SIM -> NeoIOSim(simGearRatio, simJkgMetersSquared)
        }
    }

    override fun periodic() {
        io.updateInputs(inputs)
        Logger.processInputs(logName, inputs)
    }

    fun setVoltage(volts: Double) {
        io.setVoltage(volts)
    }

    fun stop() = io.stop()

    data class Config(val canID: Int, val name: String, val sparkConfig: SparkMax.Config)
}