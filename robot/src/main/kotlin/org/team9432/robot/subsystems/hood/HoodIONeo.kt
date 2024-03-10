package org.team9432.robot.subsystems.hood

import com.revrobotics.CANSparkBase.ControlType
import com.revrobotics.CANSparkBase.IdleMode
import com.revrobotics.CANSparkLowLevel
import com.revrobotics.REVLibError
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

    private val encoderOffset = Rotation2d.fromDegrees(2.35
    )

    init {
        spark.restoreFactoryDefaults()

        for (i in 0..88) {
            spark.inverted = true
            if (spark.inverted == true) break
        }

        for (i in 0..88) {
            val errors = mutableListOf<REVLibError>()
            errors += spark.setIdleMode(IdleMode.kBrake)
            errors += spark.setSmartCurrentLimit(20)
            errors += spark.enableVoltageCompensation(12.0)
            errors += spark.getForwardLimitSwitch(SparkLimitSwitch.Type.kNormallyOpen).enableLimitSwitch(false)
            errors += spark.getReverseLimitSwitch(SparkLimitSwitch.Type.kNormallyOpen).enableLimitSwitch(false)
            errors += relativeEncoder.setPosition(0.0)
            errors += absoluteEncoder.setInverted(true)
            errors += pid.setFeedbackDevice(absoluteEncoder)
            errors += pid.setOutputRange(-0.5, 0.5)

            errors += spark.setPeriodicFramePeriod(CANSparkLowLevel.PeriodicFrame.kStatus0, 250)
            errors += spark.setPeriodicFramePeriod(CANSparkLowLevel.PeriodicFrame.kStatus3, 1000)
            errors += spark.setPeriodicFramePeriod(CANSparkLowLevel.PeriodicFrame.kStatus4, 1000)
            errors += spark.setPeriodicFramePeriod(CANSparkLowLevel.PeriodicFrame.kStatus6, 1000)

            if (errors.all { it == REVLibError.kOk }) break
        }
        spark.burnFlash()
    }

    override fun updateInputs(inputs: HoodIO.HoodIOInputs) {
        inputs.absoluteAngle = Rotation2d.fromRotations(absoluteEncoder.position / encoderToHoodRatio)
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