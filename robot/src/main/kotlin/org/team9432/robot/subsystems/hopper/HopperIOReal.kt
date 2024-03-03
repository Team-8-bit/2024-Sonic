package org.team9432.robot.subsystems.hopper

import com.revrobotics.CANSparkBase
import com.revrobotics.CANSparkBase.IdleMode
import com.revrobotics.SparkPIDController
import edu.wpi.first.wpilibj.DigitalInput
import org.team9432.lib.drivers.motors.KSparkMAX
import org.team9432.robot.Devices

class HopperIOReal: HopperIO {
    private val spark = KSparkMAX(Devices.HOPPER_ID)

    private val encoder = spark.encoder
    private val pid = spark.pidController

    init {
        spark.restoreFactoryDefaults()
        spark.inverted = true
        spark.idleMode = IdleMode.kBrake
        spark.enableVoltageCompensation(12.0)
        spark.setSmartCurrentLimit(40)
        spark.burnFlash()
    }

    override fun updateInputs(inputs: HopperIO.HopperIOInputs) {
        inputs.velocityRPM = encoder.velocity
        inputs.appliedVolts = spark.appliedOutput * spark.busVoltage
        inputs.currentAmps = spark.outputCurrent
    }

    override fun setVoltage(volts: Double) {
        spark.setVoltage(volts)
    }

    override fun setSpeed(rotationsPerMinute: Double, feedforwardVolts: Double) {
        pid.setReference(
            rotationsPerMinute,
            CANSparkBase.ControlType.kVelocity,
            0, // PID slot
            feedforwardVolts,
            SparkPIDController.ArbFFUnits.kVoltage
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