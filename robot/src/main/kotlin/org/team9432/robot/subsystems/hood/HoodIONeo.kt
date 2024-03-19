package org.team9432.robot.subsystems.hood

import com.revrobotics.CANSparkBase.IdleMode
import com.revrobotics.CANSparkLowLevel
import com.revrobotics.CANSparkMax
import com.revrobotics.REVLibError
import com.revrobotics.SparkLimitSwitch
import edu.wpi.first.math.MathUtil
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.interpolation.InterpolatingDoubleTreeMap
import edu.wpi.first.math.util.Units
import org.littletonrobotics.junction.Logger
import org.team9432.robot.Devices

class HoodIONeo: HoodIO {
    private val spark = CANSparkMax(Devices.HOOD_ID, CANSparkLowLevel.MotorType.kBrushless)

    private val encoder = spark.encoder

    private val pid = PIDController(0.0, 0.0, 0.0)

    private val gearRatio = 2.0 * (150 / 15)

    private var isClosedLoop = false

    private val ffTable = InterpolatingDoubleTreeMap()

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
            errors += encoder.setPosition(0.0)

            errors += spark.setPeriodicFramePeriod(CANSparkLowLevel.PeriodicFrame.kStatus0, 250)
            errors += spark.setPeriodicFramePeriod(CANSparkLowLevel.PeriodicFrame.kStatus3, 1000)
            errors += spark.setPeriodicFramePeriod(CANSparkLowLevel.PeriodicFrame.kStatus4, 1000)
            errors += spark.setPeriodicFramePeriod(CANSparkLowLevel.PeriodicFrame.kStatus6, 1000)

            if (errors.all { it == REVLibError.kOk }) break
        }
        pid.setTolerance(0.0)

        spark.burnFlash()

        ffTable.put(0.0, 0.0)
        ffTable.put(15.0, 10.0)
        ffTable.put(30.0, 0.0)
    }

    override fun updateInputs(inputs: HoodIO.HoodIOInputs) {
        if (isClosedLoop) {
            val r = Rotation2d.fromRotations(encoder.position * 2.0)
            spark.setVoltage(MathUtil.clamp(pid.calculate(r.rotations) + ffTable.get(pid.setpoint), -1.0, 1.0))
        }

        inputs.angle = Rotation2d.fromRotations(encoder.position / gearRatio)
        inputs.velocityRadPerSec = Units.rotationsPerMinuteToRadiansPerSecond(encoder.velocity) / gearRatio
        inputs.appliedVolts = spark.appliedOutput * spark.busVoltage
        inputs.currentAmps = spark.outputCurrent

        Logger.recordOutput("HoodDegrees", inputs.angle.degrees)
    }

    override fun setVoltage(volts: Double) {
        isClosedLoop = false
        spark.setVoltage(volts)
    }

    override fun setAngle(angle: Rotation2d) {
        isClosedLoop = true
        pid.setpoint = angle.rotations * gearRatio
    }

    override fun setPID(p: Double, i: Double, d: Double) {
        pid.setPID(p, i, d)
    }

    override fun setBrakeMode(enabled: Boolean) {
        spark.idleMode = if (enabled) IdleMode.kBrake else IdleMode.kCoast
    }

    override fun stop() = setVoltage(0.0)
}