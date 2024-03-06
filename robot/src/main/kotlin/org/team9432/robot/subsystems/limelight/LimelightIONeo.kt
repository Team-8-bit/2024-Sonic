package org.team9432.robot.subsystems.limelight

import com.revrobotics.CANSparkBase.ControlType
import com.revrobotics.CANSparkBase.IdleMode
import com.revrobotics.REVLibError
import com.revrobotics.SparkLimitSwitch
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.util.Units
import org.team9432.lib.drivers.motors.KSparkMAX
import org.team9432.robot.Devices

class LimelightIONeo: LimelightIO {
    private val spark = KSparkMAX(Devices.LIMELIGHT_MOTOR_ID)

    private val absoluteEncoder = spark.absoluteEncoder
    private val relativeEncoder = spark.encoder

    private val pid = spark.pidController

    private val gearRatio = 36 / 8

    private val encoderOffset = Rotation2d.fromDegrees(0.0)

    init {
        spark.restoreFactoryDefaults()

        for (i in 0..88) {
            spark.inverted = false
            if (spark.inverted == false) break
        }

        for (i in 0..88) {
            val errors = mutableListOf<REVLibError>()
            errors += spark.setSmartCurrentLimit(20)
            errors += spark.enableVoltageCompensation(12.0)
            errors += spark.getForwardLimitSwitch(SparkLimitSwitch.Type.kNormallyOpen).enableLimitSwitch(false)
            errors += spark.getReverseLimitSwitch(SparkLimitSwitch.Type.kNormallyOpen).enableLimitSwitch(false)
            if (errors.all { it == REVLibError.kOk }) break
        }

        relativeEncoder.position = 0.0
        absoluteEncoder.inverted = false

        pid.setFeedbackDevice(absoluteEncoder)

        spark.burnFlash()
    }

    override fun updateInputs(inputs: LimelightIO.LimelightIOInputs) {
        inputs.absoluteAngle = Rotation2d.fromRotations(absoluteEncoder.position).minus(encoderOffset)
        inputs.relativeAngle = Rotation2d.fromRotations(relativeEncoder.position / gearRatio)
        inputs.velocityDegPerSec = Units.rotationsPerMinuteToRadiansPerSecond(relativeEncoder.velocity) / gearRatio
        inputs.appliedVolts = spark.appliedOutput * spark.busVoltage
        inputs.currentAmps = spark.outputCurrent
    }

    override fun setVoltage(volts: Double) = spark.setVoltage(volts)

    override fun setAngle(angle: Rotation2d) {
        pid.setReference(angle.rotations, ControlType.kPosition)
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