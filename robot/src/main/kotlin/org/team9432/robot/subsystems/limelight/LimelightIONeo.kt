package org.team9432.robot.subsystems.limelight

import com.revrobotics.CANSparkBase.ControlType
import com.revrobotics.CANSparkBase.IdleMode
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
        spark.setCANTimeout(250)

        spark.inverted = false
        spark.setSmartCurrentLimit(20)
        spark.enableVoltageCompensation(12.0)

        relativeEncoder.position = 0.0
        absoluteEncoder.inverted = false

        pid.setFeedbackDevice(absoluteEncoder)

        spark.setCANTimeout(0)
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