package org.team9432.lib.logged.cancoder

import edu.wpi.first.math.geometry.Rotation2d
import org.littletonrobotics.junction.Logger
import org.team9432.lib.State
import org.team9432.lib.State.Mode.*

class LoggedCancoder(private val config: Config) {
    private val io: LoggedCancoderIO
    private val inputs = LoggedCancoderIO.CancoderIOInputs(config.additionalQualifier)

    init {
        io = when (State.mode) {
            REAL, REPLAY -> LoggedCancoderIOReal(config)
            SIM -> LoggedCancoderIOSim(config)
        }
    }

    fun updateAndRecordInputs(): LoggedCancoderIO.CancoderIOInputs {
        io.updateInputs(inputs)
        Logger.processInputs(config.logName, inputs)
        return inputs
    }

    /** A class describing the configuration options of a logged cancoder */
    data class Config(
        val canID: Int,
        val deviceName: String,
        val logName: String,
        val gearRatio: Double = 1.0,
        val additionalQualifier: String = "",
        val encoderOffset: Rotation2d,
        val simPositionSupplier: () -> Rotation2d = { Rotation2d() },
    )
}