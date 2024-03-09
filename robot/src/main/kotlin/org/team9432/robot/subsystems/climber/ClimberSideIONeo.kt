package org.team9432.robot.subsystems.climber

import com.revrobotics.CANSparkBase.ControlType
import com.revrobotics.CANSparkBase.IdleMode
import com.revrobotics.CANSparkLowLevel
import com.revrobotics.REVLibError
import com.revrobotics.SparkLimitSwitch
import com.revrobotics.SparkPIDController.ArbFFUnits
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.util.Units
import edu.wpi.first.wpilibj.DigitalInput
import org.team9432.lib.drivers.motors.KSparkMAX

class ClimberSideIONeo(override val climberSide: ClimberSideIO.ClimberSide): ClimberSideIO {
    private val spark = KSparkMAX(climberSide.motorID)
    private val limit = DigitalInput(climberSide.limitPort)

    private val encoder = spark.encoder
    private val pid = spark.pidController

    private val gearRatio = (50 / 10) * (50 / 18) * (50 / 18)

    init {
        spark.restoreFactoryDefaults()

        for (i in 0..88) {
            spark.inverted = climberSide.inverted
            if (spark.inverted == climberSide.inverted) break
        }

        for (i in 0..88) {
            val errors = mutableListOf<REVLibError>()
            errors += spark.setIdleMode(IdleMode.kBrake)
            errors += spark.enableVoltageCompensation(12.0)
            errors += spark.setSmartCurrentLimit(40)
            errors += spark.getForwardLimitSwitch(SparkLimitSwitch.Type.kNormallyOpen).enableLimitSwitch(false)
            errors += spark.getReverseLimitSwitch(SparkLimitSwitch.Type.kNormallyOpen).enableLimitSwitch(false)

            errors += spark.setPeriodicFramePeriod(CANSparkLowLevel.PeriodicFrame.kStatus0, 1000)
            errors += spark.setPeriodicFramePeriod(CANSparkLowLevel.PeriodicFrame.kStatus1, 1000)
            errors += spark.setPeriodicFramePeriod(CANSparkLowLevel.PeriodicFrame.kStatus2, 1000)
            errors += spark.setPeriodicFramePeriod(CANSparkLowLevel.PeriodicFrame.kStatus3, 1000)
            errors += spark.setPeriodicFramePeriod(CANSparkLowLevel.PeriodicFrame.kStatus4, 1000)
            errors += spark.setPeriodicFramePeriod(CANSparkLowLevel.PeriodicFrame.kStatus5, 1000)
            errors += spark.setPeriodicFramePeriod(CANSparkLowLevel.PeriodicFrame.kStatus6, 1000)
            if (errors.all { it == REVLibError.kOk }) break
        }

        spark.burnFlash()
    }

    override fun updateInputs(inputs: ClimberSideIO.ClimberSideIOInputs) {
        inputs.position = Rotation2d.fromRotations(encoder.position / gearRatio)
        inputs.velocityRadPerSec = Units.rotationsPerMinuteToRadiansPerSecond(encoder.velocity / gearRatio)
        inputs.atLimit = limit.get()
        inputs.appliedVolts = spark.appliedOutput * spark.busVoltage
        inputs.currentAmps = spark.outputCurrent
    }

    override fun setVoltage(volts: Double) = spark.setVoltage(volts)

    override fun setAngle(angle: Rotation2d, feedforwardVolts: Double) {
        pid.setReference(
            angle.rotations,
            ControlType.kPosition,
            0, // PID slot
            feedforwardVolts,
            ArbFFUnits.kVoltage
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

    override fun stop() {
        spark.setVoltage(0.0)
    }
}