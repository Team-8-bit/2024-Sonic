package org.team9432.lib.motors.neo

import edu.wpi.first.math.geometry.Rotation2d
import org.littletonrobotics.junction.Logger
import org.team9432.Robot
import org.team9432.Robot.Mode.*
import org.team9432.lib.commandbased.KPeriodic
import org.team9432.lib.wrappers.Spark

class NEO(private val config: Config): KPeriodic() {
    private val io: NeoIO
    val inputs = LoggedNEOIOInputs()

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

    fun setVoltage(volts: Double) = io.setVoltage(volts)
    fun setAngle(angle: Rotation2d) = io.setAngle(angle)
    fun setSpeed(rpm: Int) = io.setSpeed(rpm)
    fun setPID(p: Double, i: Double, d: Double) = io.setPID(p, i, d)
    fun setBrakeMode(enabled: Boolean) = io.setBrakeMode(enabled)
    fun resetEncoder(newAngle: Rotation2d = Rotation2d()) = io.resetEncoder(newAngle)
    fun stop() = io.stop()

    enum class ControlMode {
        VOLTAGE, POSITION, VELOCITY
    }

    data class Config(
        val canID: Int,
        val motorType: Spark.MotorType,
        val name: String,
        val logName: String,
        val gearRatio: Double,
        val feedForwardSupplier: (Double) -> Double = { 0.0 },
        val simJkgMetersSquared: Double,
        val sparkConfig: Spark.Config
    )
}