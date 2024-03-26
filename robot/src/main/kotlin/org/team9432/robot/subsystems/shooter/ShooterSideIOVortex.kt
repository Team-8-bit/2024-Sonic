package org.team9432.robot.subsystems.shooter

import com.revrobotics.CANSparkBase.IdleMode
import com.revrobotics.CANSparkFlex
import com.revrobotics.CANSparkLowLevel
import com.revrobotics.REVLibError
import com.revrobotics.SparkLimitSwitch
import edu.wpi.first.math.MathUtil
import edu.wpi.first.math.controller.PIDController
import org.team9432.lib.wrappers.SparkFlex

class ShooterSideIOVortex(override val shooterSide: ShooterSideIO.ShooterSide): ShooterSideIO {
    private val spark = SparkFlex(shooterSide.motorID, "${shooterSide.name} Shooter Motor")

    private val encoder = spark.encoder
    private val pid = PIDController(0.0, 0.0, 0.0)

    private val gearRatio = 0.5

    private var isClosedLoop = false
    private var feedforwardVolts = 0.0

    init {
        val config = SparkFlex.Config(
            inverted = shooterSide.inverted,
            idleMode = IdleMode.kCoast,
            smartCurrentLimit = 80
        )

        spark.applyConfig(config)
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