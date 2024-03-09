package org.team9432.robot.subsystems.amp

import com.revrobotics.*
import org.team9432.lib.drivers.motors.KSparkMAX
import org.team9432.robot.Devices

class AmpIOReal : AmpIO {
    private val spark = KSparkMAX(Devices.AMP_ID, CANSparkLowLevel.MotorType.kBrushless)

    private val encoder = spark.encoder
    private val pid = spark.pidController

    init {
        spark.restoreFactoryDefaults()

        for (i in 0..88) {
            spark.inverted = false
            if (spark.inverted == false) break
        }

        for (i in 0..88) {
            val errors = mutableListOf<REVLibError>()
            errors += spark.setIdleMode(CANSparkBase.IdleMode.kCoast)
            errors += spark.enableVoltageCompensation(12.0)
            errors += spark.setSmartCurrentLimit(60)
            errors += spark.getForwardLimitSwitch(SparkLimitSwitch.Type.kNormallyOpen).enableLimitSwitch(false)
            errors += spark.getReverseLimitSwitch(SparkLimitSwitch.Type.kNormallyOpen).enableLimitSwitch(false)

            errors += spark.setPeriodicFramePeriod(CANSparkLowLevel.PeriodicFrame.kStatus0, 250)
            errors += spark.setPeriodicFramePeriod(CANSparkLowLevel.PeriodicFrame.kStatus3, 1000)
            errors += spark.setPeriodicFramePeriod(CANSparkLowLevel.PeriodicFrame.kStatus4, 1000)
            errors += spark.setPeriodicFramePeriod(CANSparkLowLevel.PeriodicFrame.kStatus5, 1000)
            errors += spark.setPeriodicFramePeriod(CANSparkLowLevel.PeriodicFrame.kStatus6, 1000)
            if (errors.all { it == REVLibError.kOk }) break
        }

        spark.burnFlash()
    }


    override fun updateInputs(inputs: AmpIO.AmpIOInputs) {
        inputs.velocityRPM = encoder.velocity
        inputs.appliedVolts = spark.appliedOutput * spark.busVoltage
        inputs.currentAmps = spark.outputCurrent
    }

    override fun setVoltage(volts: Double) {
        spark.setVoltage(volts)
    }

    override fun setSpeed(rpm: Double, ffVolts: Double) {
        pid.setReference(
            rpm,
            CANSparkBase.ControlType.kVelocity,
            0, // PID slot
            ffVolts,
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
