package org.team9432.lib.motors.neo

import edu.wpi.first.math.MathUtil
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.util.Units
import org.team9432.lib.wrappers.Spark

class NeoIONeo(val config: NEO.Config): NeoIO {
    private val spark = Spark(config.canID, config.name, config.motorType)

    private val encoder = spark.encoder

    private var isClosedLoop = false

    private val pid = PIDController(0.0, 0.0, 0.0)

    init {
        spark.applyConfig(config.sparkConfig)

        pid.setTolerance(0.0)
    }

    override fun updateInputs(inputs: NeoIO.NEOIOInputs) {
        if (isClosedLoop) {
            val r = Rotation2d.fromRotations(encoder.position)
            spark.setVoltage(MathUtil.clamp(pid.calculate(r.rotations) + config.feedForwardSupplier.invoke(pid.setpoint), -1.0, 1.0))
        }

        inputs.angle = Rotation2d.fromRotations(encoder.position) / config.gearRatio
        inputs.velocityRadPerSec = Units.rotationsPerMinuteToRadiansPerSecond(encoder.velocity) / config.gearRatio
        inputs.appliedVolts = spark.appliedOutput * spark.busVoltage
        inputs.currentAmps = spark.outputCurrent
    }

    override fun setVoltage(volts: Double) {
        isClosedLoop = false
        spark.setVoltage(volts)
    }

    override fun setAngle(angle: Rotation2d) {
        isClosedLoop = true
        pid.setpoint = angle.rotations * config.gearRatio
    }

    override fun setPID(p: Double, i: Double, d: Double) = pid.setPID(p, i, d)
    override fun stop() = setVoltage(0.0)
}