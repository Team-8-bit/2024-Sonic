package org.team9432.lib.motors.neo

import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.util.Units
import org.team9432.lib.wrappers.SparkMax

class NeoIONeo(config: NEO.Config): NeoIO {
    private val spark = SparkMax(config.canID, config.name)

    private val encoder = spark.encoder

    init {
        spark.applyConfig(config.sparkConfig)
    }

    override fun updateInputs(inputs: NeoIO.NEOIOInputs) {
        inputs.angle = Rotation2d.fromRotations(encoder.position)
        inputs.velocityRadPerSec = Units.rotationsPerMinuteToRadiansPerSecond(encoder.velocity)
        inputs.appliedVolts = spark.appliedOutput * spark.busVoltage
        inputs.currentAmps = spark.outputCurrent
    }

    override fun setVoltage(volts: Double) {
        spark.setVoltage(volts)
    }

    override fun stop() {
        spark.setVoltage(0.0)
    }
}