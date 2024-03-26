package org.team9432.lib.motors.neo

import com.revrobotics.CANSparkBase.IdleMode
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.util.Units
import org.team9432.lib.wrappers.SparkMax
import org.team9432.robot.Devices

class NeoIONeo(canID: Int, name: String): NeoIO {
    private val spark = SparkMax(canID, name)

    private val encoder = spark.encoder

    init {
        val config = SparkMax.Config(
            inverted = true,
            idleMode = IdleMode.kBrake,
            smartCurrentLimit = 60
        )

        spark.applyConfig(config)
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