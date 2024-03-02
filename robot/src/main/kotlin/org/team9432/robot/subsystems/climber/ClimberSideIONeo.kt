package org.team9432.robot.subsystems.climber

import com.revrobotics.CANSparkBase.ControlType
import com.revrobotics.CANSparkBase.IdleMode
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
        spark.inverted = climberSide.inverted
        spark.idleMode = IdleMode.kBrake
        spark.enableVoltageCompensation(12.0)
        spark.setSmartCurrentLimit(40)
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