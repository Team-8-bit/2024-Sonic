package org.team9432.robot.subsystems.hopper

import com.revrobotics.CANSparkBase.IdleMode
import org.team9432.lib.wrappers.SparkMax
import org.team9432.robot.Devices

class HopperIONeo: HopperIO {
    private val spark = SparkMax(Devices.HOPPER_ID, "Hopper Motor")

    private val encoder = spark.encoder

    init {
        val config = SparkMax.Config(
            inverted = true,
            idleMode = IdleMode.kBrake,
            smartCurrentLimit = 60
        )

        spark.applyConfig(config)
    }

    override fun updateInputs(inputs: HopperIO.HopperIOInputs) {
        inputs.velocityRPM = encoder.velocity
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