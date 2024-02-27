package org.team9432.robot.subsystems.intake

import com.revrobotics.CANSparkBase.ControlType
import com.revrobotics.CANSparkBase.IdleMode
import com.revrobotics.SparkPIDController.ArbFFUnits
import org.team9432.lib.drivers.motors.KSparkMAX

class IntakeSideIONeo(override val intakeSide: IntakeSideIO.IntakeSide): IntakeSideIO {
    private val spark = KSparkMAX(intakeSide.motorID)

    private val encoder = spark.encoder
    private val pid = spark.pidController

    private val gearRatio = 2

    init {
        spark.restoreFactoryDefaults()
        spark.inverted = intakeSide.inverted
        spark.idleMode = IdleMode.kCoast
        spark.enableVoltageCompensation(12.0)
        spark.setSmartCurrentLimit(80)
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

    override fun setSpeed(rotationsPerMinute: Double, feedforwardVolts: Double) {
        pid.setReference(
            rotationsPerMinute * gearRatio,
            ControlType.kVelocity,
            0, // PID slot
            feedforwardVolts,
            ArbFFUnits.kVoltage
        )
    }

    override fun setPID(p: Double, i: Double, d: Double) {
        pid.setP(p, 0)
        pid.setI(i, 0)
        pid.setD(d, 0)
        pid.setFF(0.0, 0)
    }

    override fun stop() {
        spark.setVoltage(0.0)
    }
}