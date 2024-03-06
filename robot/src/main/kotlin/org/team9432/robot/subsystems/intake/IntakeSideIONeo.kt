package org.team9432.robot.subsystems.intake

import com.revrobotics.CANSparkBase.ControlType
import com.revrobotics.CANSparkBase.IdleMode
import com.revrobotics.REVLibError
import com.revrobotics.SparkLimitSwitch
import com.revrobotics.SparkPIDController.ArbFFUnits
import org.team9432.lib.drivers.motors.KSparkMAX

class IntakeSideIONeo(override val intakeSide: IntakeSideIO.IntakeSide): IntakeSideIO {
    private val spark = KSparkMAX(intakeSide.motorID)

    private val encoder = spark.encoder

    private val gearRatio = 2

    init {
        spark.restoreFactoryDefaults()

        for (i in 0..88) {
            spark.inverted = intakeSide.inverted
            if (spark.inverted == intakeSide.inverted) break
        }

        for (i in 0..88) {
            val errors = mutableListOf<REVLibError>()
            errors += spark.setIdleMode(IdleMode.kCoast)
            errors += spark.enableVoltageCompensation(12.0)
            errors += spark.setSmartCurrentLimit(80)
            errors += spark.getForwardLimitSwitch(SparkLimitSwitch.Type.kNormallyOpen).enableLimitSwitch(false)
            errors += spark.getReverseLimitSwitch(SparkLimitSwitch.Type.kNormallyOpen).enableLimitSwitch(false)
            if (errors.all { it == REVLibError.kOk }) break
        }

        spark.burnFlash()
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