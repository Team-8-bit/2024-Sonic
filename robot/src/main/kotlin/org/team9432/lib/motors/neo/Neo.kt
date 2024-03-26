package org.team9432.lib.motors.neo

import org.littletonrobotics.junction.Logger
import org.team9432.Robot
import org.team9432.Robot.Mode.*
import org.team9432.lib.commandbased.KPeriodic
import org.team9432.lib.wrappers.SparkMax

class NEO(private val config: Config): KPeriodic() {
    private val io: NeoIO
    private val inputs = LoggedNEOIOInputs()

    init {
        io = when (Robot.mode) {
            REAL, REPLAY -> NeoIONeo(config)
            SIM -> NeoIOSim(config)
        }
    }

    override fun periodic() {
        io.updateInputs(inputs)
        Logger.processInputs(config.logName, inputs)
    }

    fun setVoltage(volts: Double) {
        io.setVoltage(volts)
    }

    fun stop() = io.stop()

    data class Config(
        val canID: Int,
        val name: String,
        val logName: String,
        val simGearRatio: Double,
        val simJkgMetersSquared: Double,
        val sparkConfig: SparkMax.Config
    )
}