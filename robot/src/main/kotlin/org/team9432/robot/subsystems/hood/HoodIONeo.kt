package org.team9432.robot.subsystems.hood

import com.revrobotics.CANSparkBase.ControlType
import com.revrobotics.CANSparkBase.IdleMode
import com.revrobotics.SparkLimitSwitch
import com.revrobotics.SparkPIDController.ArbFFUnits
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.util.Units
import org.littletonrobotics.junction.Logger
import org.team9432.lib.drivers.motors.KSparkMAX
import org.team9432.robot.Devices

class HoodIONeo: HoodIO {
    private val spark = KSparkMAX(Devices.HOOD_ID)

    private val absoluteEncoder = spark.absoluteEncoder
    private val relativeEncoder = spark.encoder

    private val pid = spark.pidController

    private val motorToHoodRatio = 2.0 * (150 / 15)
    private val encoderToHoodRatio = 150 / 15

    private val encoderOffset = Rotation2d(0.07)

    init {
        spark.restoreFactoryDefaults()

        spark.idleMode = IdleMode.kBrake

        spark.inverted = true
        spark.setSmartCurrentLimit(20)
        spark.enableVoltageCompensation(12.0)

        relativeEncoder.position = 0.0
        absoluteEncoder.inverted = true

        spark.getForwardLimitSwitch(SparkLimitSwitch.Type.kNormallyOpen).enableLimitSwitch(false)
        spark.getReverseLimitSwitch(SparkLimitSwitch.Type.kNormallyOpen).enableLimitSwitch(false)

        pid.setFeedbackDevice(absoluteEncoder)

        spark.burnFlash()
    }

    override fun updateInputs(inputs: HoodIO.HoodIOInputs) {
        inputs.absoluteAngle = Rotation2d.fromRotations(absoluteEncoder.position / encoderToHoodRatio).minus(encoderOffset)
        inputs.relativeAngle = Rotation2d.fromRotations(relativeEncoder.position / motorToHoodRatio)
        inputs.velocityDegPerSec = Units.rotationsPerMinuteToRadiansPerSecond(relativeEncoder.velocity) / motorToHoodRatio
        inputs.appliedVolts = spark.appliedOutput * spark.busVoltage
        inputs.currentAmps = spark.outputCurrent

        Logger.recordOutput("HoodDegrees", inputs.absoluteAngle.degrees)
    }

    override fun setVoltage(volts: Double) = spark.setVoltage(volts)

    override fun setAngle(angle: Rotation2d, feedforwardVolts: Double) {
        pid.setReference(
            angle.plus(encoderOffset).rotations * encoderToHoodRatio,
            ControlType.kPosition
        )
    }

    override fun setPID(p: Double, i: Double, d: Double) {
        pid.setP(p, 0)
        pid.setI(i, 0)
        pid.setD(d, 0)
        pid.setFF(0.0, 0)
    }

    override fun setBrakeMode(enabled: Boolean) {
        spark.idleMode = if (enabled) IdleMode.kBrake else IdleMode.kCoast
    }

    override fun stop() = spark.stopMotor()
}