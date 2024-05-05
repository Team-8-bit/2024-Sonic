package org.team9432.lib.wrappers.neo

import edu.wpi.first.math.geometry.Rotation2d
import org.littletonrobotics.junction.Logger
import org.team9432.lib.State
import org.team9432.lib.State.Mode.*
import org.team9432.lib.wrappers.Spark

/**
 * A generic neo wrapper that safely manages access to all config settings and the integrated encoder,
 * logs all robot code inputs, and uses a simulated motor when necessary.
 */
class LoggedNeo(val config: Config) {
    private val io: LoggedNeoIO
    private val inputs = LoggedNeoIO.NEOIOInputs(config.additionalQualifier)

    init {
        io = when (State.mode) {
            REAL, REPLAY -> LoggedNeoIOReal(config)
            SIM -> LoggedNeoIOSim(config)
        }
    }

    fun updateAndRecordInputs(): LoggedNeoIO.NEOIOInputs {
        io.updateInputs(inputs)
        Logger.processInputs(config.logName, inputs)
        return inputs
    }

    /** Run open loop at the specified voltage */
    fun setVoltage(volts: Double) = io.setVoltage(volts)

    /** Set the motor in brake mode */
    fun setBrakeMode(enabled: Boolean) = io.setBrakeMode(enabled)

    /** Sets the integrated encoder to the specified angle. Defaults to 0. */
    fun resetEncoder(newAngle: Rotation2d = Rotation2d()) = io.resetEncoder(newAngle)

    /** Stops the motor */
    fun stop() = io.stop()

    /** A class describing the configuration options of a neo */
    data class Config(
        val canID: Int,
        val motorType: Spark.MotorType,
        val deviceName: String,
        val logName: String,
        val gearRatio: Double,
        val additionalQualifier: String = "",
        val simJkgMetersSquared: Double,
        val sparkConfig: Spark.Config,
    )
}