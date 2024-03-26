package org.team9432.lib.motors.neo

import edu.wpi.first.math.geometry.Rotation2d
import org.littletonrobotics.junction.Logger
import org.team9432.lib.State
import org.team9432.lib.State.Mode.*
import org.team9432.lib.commandbased.KPeriodic
import org.team9432.lib.wrappers.Spark

/**
 * A generic neo wrapper that safely manages access to all config settings and the integrated encoder,
 * logs all robot code inputs, and uses a simulated motor when necessary.
 */
class Neo(private val config: Config): KPeriodic() {
    private val io: NeoIO
    val inputs = LoggedNEOIOInputs()

    init {
        io = when (State.mode) {
            REAL, REPLAY -> NeoIOReal(config)
            SIM -> NeoIOSim(config)
        }
    }

    override fun periodic() {
        io.updateInputs(inputs)
        Logger.processInputs(config.logName, inputs)
    }

    /** Run open loop at the specified voltage */
    fun setVoltage(volts: Double) = io.setVoltage(volts)

    /** Run closed loop position control */
    fun setAngle(angle: Rotation2d) = io.setAngle(angle)

    /** Run closed loop velocity control */
    fun setSpeed(rpm: Int) = io.setSpeed(rpm)

    /** Set PID constants */
    fun setPID(p: Double, i: Double, d: Double) = io.setPID(p, i, d)

    /** Set the motor in brake mode */
    fun setBrakeMode(enabled: Boolean) = io.setBrakeMode(enabled)

    /** Sets the integrated encoder to the specified angle. Defaults to 0. */
    fun resetEncoder(newAngle: Rotation2d = Rotation2d()) = io.resetEncoder(newAngle)

    /** Stops the motor */
    fun stop() = io.stop()

    enum class ControlMode {
        VOLTAGE, POSITION, VELOCITY
    }

    /** A class describing the configuration options of a neo */
    data class Config(
        val canID: Int,
        val motorType: Spark.MotorType,
        val name: String,
        val logName: String,
        val gearRatio: Double,
        val feedForwardSupplier: (Double) -> Double = { 0.0 },
        val simJkgMetersSquared: Double,
        val sparkConfig: Spark.Config,
    )
}