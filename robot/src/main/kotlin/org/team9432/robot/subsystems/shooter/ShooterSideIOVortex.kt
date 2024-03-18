package org.team9432.robot.subsystems.shooter

import com.revrobotics.CANSparkBase.IdleMode
import com.revrobotics.CANSparkFlex
import com.revrobotics.CANSparkLowLevel
import com.revrobotics.REVLibError
import com.revrobotics.SparkLimitSwitch
import edu.wpi.first.math.MathUtil
import edu.wpi.first.math.controller.PIDController

class ShooterSideIOVortex(override val shooterSide: ShooterSideIO.ShooterSide): ShooterSideIO {
    private val spark = CANSparkFlex(shooterSide.motorID, CANSparkLowLevel.MotorType.kBrushless)

    private val encoder = spark.encoder
    private val pid = PIDController(0.0, 0.0, 0.0)

    private val gearRatio = 0.5

    private var isClosedLoop = false
    private var feedforwardVolts = 0.0

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

            errors += spark.setPeriodicFramePeriod(CANSparkLowLevel.PeriodicFrame.kStatus0, 250)
            errors += spark.setPeriodicFramePeriod(CANSparkLowLevel.PeriodicFrame.kStatus3, 1000)
            errors += spark.setPeriodicFramePeriod(CANSparkLowLevel.PeriodicFrame.kStatus4, 1000)
            errors += spark.setPeriodicFramePeriod(CANSparkLowLevel.PeriodicFrame.kStatus5, 1000)
            errors += spark.setPeriodicFramePeriod(CANSparkLowLevel.PeriodicFrame.kStatus6, 1000)
            if (errors.all { it == REVLibError.kOk }) break
        }

        spark.burnFlash()
    }

    override fun updateInputs(inputs: ShooterSideIO.ShooterSideIOInputs) {
        if (isClosedLoop) {
            spark.set(MathUtil.clamp(pid.calculate(encoder.velocity) + feedforwardVolts, -12.0, 12.0))
        }

        inputs.velocityRPM = encoder.velocity / gearRatio
        inputs.appliedVolts = spark.appliedOutput * spark.busVoltage
        inputs.currentAmps = spark.outputCurrent
    }

    override fun setVoltage(volts: Double) {
        isClosedLoop = false
        spark.setVoltage(volts)
    }

    override fun setSpeed(rotationsPerMinute: Double, feedforwardVolts: Double) {
        isClosedLoop = true
        pid.setpoint = rotationsPerMinute * gearRatio
        this.feedforwardVolts = feedforwardVolts
    }

    override fun setPID(p: Double, i: Double, d: Double) {
        pid.setPID(p, i, d)
    }

    override fun stop() {
        setVoltage(0.0)
    }
}