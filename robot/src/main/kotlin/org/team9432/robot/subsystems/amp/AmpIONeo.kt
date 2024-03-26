package org.team9432.robot.subsystems.amp

import com.revrobotics.*
import org.team9432.lib.wrappers.SparkMax
import org.team9432.robot.Devices

class AmpIONeo: AmpIO {
    private val spark = SparkMax(Devices.AMP_ID, "Amp Motor")

    private val encoder = spark.encoder

    init {
        val config = SparkMax.Config(
            inverted = true,
            idleMode = CANSparkBase.IdleMode.kCoast,
            smartCurrentLimit = 60
        )

        spark.applyConfig(config)
    }

    override fun updateInputs(inputs: AmpIO.AmpIOInputs) {
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
