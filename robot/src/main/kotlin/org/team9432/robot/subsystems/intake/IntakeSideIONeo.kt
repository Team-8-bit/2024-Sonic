package org.team9432.robot.subsystems.intake

import com.revrobotics.CANSparkBase.IdleMode
import com.revrobotics.CANSparkLowLevel
import com.revrobotics.CANSparkMax
import com.revrobotics.REVLibError
import com.revrobotics.SparkLimitSwitch
import org.team9432.lib.wrappers.SparkMax

class IntakeSideIONeo(override val intakeSide: IntakeSideIO.IntakeSide): IntakeSideIO {
    private val spark = SparkMax(intakeSide.motorID, "${intakeSide.name} Intake Motor")

    private val encoder = spark.encoder

    private val gearRatio = 2

    init {
        val config = SparkMax.Config(
            inverted = intakeSide.inverted,
            idleMode = IdleMode.kCoast,
            smartCurrentLimit = 80
        )

        spark.applyConfig(config)
    }

    override fun updateInputs(inputs: IntakeSideIO.IntakeSideIOInputs) {
        inputs.velocityRPM = encoder.velocity / gearRatio
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