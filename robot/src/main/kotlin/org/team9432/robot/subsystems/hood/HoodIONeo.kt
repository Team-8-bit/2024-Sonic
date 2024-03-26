package org.team9432.robot.subsystems.hood

import com.revrobotics.CANSparkBase.IdleMode
import edu.wpi.first.math.MathUtil
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.interpolation.InterpolatingDoubleTreeMap
import edu.wpi.first.math.util.Units
import org.littletonrobotics.junction.Logger
import org.team9432.lib.wrappers.SparkMax
import org.team9432.robot.Devices

class HoodIONeo: HoodIO {
    private val spark = SparkMax(Devices.HOOD_ID, "Hood Motor")

    private val encoder = spark.encoder

    private val pid = PIDController(0.0, 0.0, 0.0)

    private val gearRatio = 2.0 * (150 / 15)

    private var isClosedLoop = false

    private val ffTable = InterpolatingDoubleTreeMap()

    init {
        val config = SparkMax.Config(
            inverted = true,
            idleMode = IdleMode.kBrake,
            smartCurrentLimit = 20,
            voltageCompensation = 12.0,
            forwardLimitSwitchEnabled = false,
            reverseLimitSwitchEnabled = false,

            periodicFramePeriod0 = 1000,
            periodicFramePeriod3 = 1000,
            periodicFramePeriod4 = 1000,
            periodicFramePeriod5 = 1000,
            periodicFramePeriod6 = 1000,
        )

        spark.applyConfig(config)

        pid.setTolerance(0.0)

        ffTable.put(0.0, 0.0)
        ffTable.put(15.0, 10.0)
        ffTable.put(30.0, 0.0)
    }

    override fun updateInputs(inputs: HoodIO.HoodIOInputs) {
        if (isClosedLoop) {
            val r = Rotation2d.fromRotations(encoder.position)
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
        spark.applySetting("Idle Mode") { spark.setIdleMode(if (enabled) IdleMode.kBrake else IdleMode.kCoast) }
    }

    override fun stop() = setVoltage(0.0)
}