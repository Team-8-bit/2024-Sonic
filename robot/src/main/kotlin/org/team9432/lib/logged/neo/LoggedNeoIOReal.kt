package org.team9432.lib.logged.neo

import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.util.Units
import org.littletonrobotics.junction.Logger
import org.team9432.lib.wrappers.Spark

class LoggedNeoIOReal(val config: LoggedNeo.Config): LoggedNeoIO {
    private val spark = Spark(config.canID, config.deviceName, config.motorType)

    private val encoder = spark.encoder

    private var controlMode = LoggedNeo.ControlMode.VOLTAGE

    private val pid = PIDController(0.0, 0.0, 0.0)

    init {
        spark.applyConfig(config.sparkConfig)

        pid.setTolerance(0.0)
    }

    override fun updateInputs(inputs: LoggedNeoIO.NEOIOInputs) {
        when (controlMode) {
            LoggedNeo.ControlMode.VOLTAGE -> {}
            LoggedNeo.ControlMode.POSITION -> {} //spark.setVoltage(pid.calculate(encoder.position) + config.feedForwardSupplier.invoke(pid.setpoint))
            LoggedNeo.ControlMode.VELOCITY -> {} //spark.setVoltage(pid.calculate(Units.rotationsPerMinuteToRadiansPerSecond(encoder.velocity)) + config.feedForwardSupplier.invoke(pid.setpoint))
        }

        inputs.angle = Rotation2d.fromRotations(encoder.position) / config.gearRatio
        inputs.velocityRadPerSec = Units.rotationsPerMinuteToRadiansPerSecond(encoder.velocity) / config.gearRatio
        inputs.appliedVolts = spark.appliedOutput * spark.busVoltage
        inputs.currentAmps = spark.outputCurrent

        Logger.recordOutput("${config.logName}/${config.additionalQualifier}ControlMode", controlMode)
    }

    override fun setVoltage(volts: Double) {
        controlMode = LoggedNeo.ControlMode.VOLTAGE
        spark.setVoltage(volts)
        Logger.recordOutput("${config.logName}/${config.additionalQualifier}VoltsSet", volts)
    }

    override fun setAngle(angle: Rotation2d) {
        controlMode = LoggedNeo.ControlMode.POSITION
        pid.setpoint = angle.rotations * config.gearRatio
    }

    override fun setSpeed(radPerSecond: Double) {
        controlMode = LoggedNeo.ControlMode.VELOCITY
        pid.setpoint = radPerSecond
    }

    override fun setPID(p: Double, i: Double, d: Double) = pid.setPID(p, i, d)
    override fun setBrakeMode(enabled: Boolean) = spark.setBrakeMode(enabled)
    override fun stop() = setVoltage(0.0)
}