package org.team9432.robot.subsystems.amp

import com.revrobotics.CANSparkBase
import com.revrobotics.CANSparkLowLevel
import com.revrobotics.CANSparkMax
import com.revrobotics.SparkPIDController
import edu.wpi.first.wpilibj2.command.SubsystemBase
import org.team9432.lib.drivers.motors.KSparkFlex
import org.team9432.robot.Devices

class AmpIOReal: AmpIO, SubsystemBase() {
    private val spark = CANSparkMax(Devices.AMP_ID, CANSparkLowLevel.MotorType.kBrushless)

    private val encoder = spark.encoder
    private val pid = spark.pidController

    private val gearRatio = 0.5
    override fun updateInputs(inputs: AmpIO.AmpIOInputs) {
        inputs.velocityRPM = encoder.velocity / gearRatio
        inputs.appliedVolts = spark.appliedOutput * spark.busVoltage
        inputs.currentAmps = spark.outputCurrent
    }
    override fun setVoltage(volts: Double) {
        spark.setVoltage(volts)
    }
    override fun setSpeed(rpm: Double, ffVolts: Double) {
        pid.setReference(
            rpm * gearRatio,
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
        spark.stopMotor()
    }

}
