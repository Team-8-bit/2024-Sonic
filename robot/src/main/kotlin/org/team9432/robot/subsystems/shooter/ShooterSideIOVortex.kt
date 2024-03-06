package org.team9432.robot.subsystems.shooter

import com.revrobotics.CANSparkBase.*
import com.revrobotics.REVLibError
import com.revrobotics.SparkLimitSwitch
import com.revrobotics.SparkPIDController.ArbFFUnits
import org.team9432.lib.drivers.motors.KSparkFlex

class ShooterSideIOVortex(override val shooterSide: ShooterSideIO.ShooterSide): ShooterSideIO {
    private val spark = KSparkFlex(shooterSide.motorID)

    private val encoder = spark.encoder
    private val pid = spark.pidController

    private val gearRatio = 0.5

    init {
        spark.restoreFactoryDefaults()

        for (i in 0..88) {
            spark.inverted = shooterSide.inverted
            if (spark.inverted == shooterSide.inverted) break
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

    override fun updateInputs(inputs: ShooterSideIO.ShooterSideIOInputs) {
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