package org.team9432.robot.subsystems.amp

import com.revrobotics.CANSparkBase
import com.revrobotics.CANSparkLowLevel
import com.revrobotics.SparkPIDController
import org.team9432.lib.drivers.motors.KSparkMAX
import org.team9432.robot.Devices

class AmpIOReal: AmpIO {
    private val spark = KSparkMAX(Devices.AMP_ID, CANSparkLowLevel.MotorType.kBrushless)

    private val encoder = spark.encoder
    private val pid = spark.pidController

    init {
        spark.restoreFactoryDefaults()

        spark.idleMode = CANSparkBase.IdleMode.kCoast

        spark.enableVoltageCompensation(12.0)

        spark.setSmartCurrentLimit(30)

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
