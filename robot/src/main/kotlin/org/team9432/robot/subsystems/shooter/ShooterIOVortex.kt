package org.team9432.robot.subsystems.shooter

import com.revrobotics.CANSparkBase.*
import com.revrobotics.SparkPIDController.ArbFFUnits
import org.team9432.lib.drivers.motors.KSparkFlex
import org.team9432.robot.Devices

class ShooterIOVortex: ShooterIO {
    private val left = KSparkFlex(Devices.LEFT_SHOOTER_ID)
    private val right = KSparkFlex(Devices.RIGHT_SHOOTER_ID)

    private val leftEncoder = left.encoder
    private val leftPid = left.pidController

    private val rightEncoder = right.encoder
    private val rightPid = right.pidController

    private val gearRatio = 0.5

    init {
        left.restoreFactoryDefaults()
        right.restoreFactoryDefaults()

        left.inverted = false
        right.inverted = true

        left.idleMode = IdleMode.kCoast
        right.idleMode = IdleMode.kCoast

        left.enableVoltageCompensation(12.0)
        right.enableVoltageCompensation(12.0)

        left.setSmartCurrentLimit(80)
        right.setSmartCurrentLimit(80)

        left.burnFlash()
        right.burnFlash()
    }

    override fun updateInputs(inputs: ShooterIO.ShooterIOInputs) {
        inputs.leftVelocityRPM = leftEncoder.velocity / gearRatio
        inputs.leftAppliedVolts = left.appliedOutput * left.busVoltage
        inputs.leftCurrentAmps = left.outputCurrent

        inputs.rightVelocityRPM = rightEncoder.velocity / gearRatio
        inputs.rightAppliedVolts = right.appliedOutput * right.busVoltage
        inputs.rightCurrentAmps = right.outputCurrent
    }

    override fun setVoltage(leftVolts: Double, rightVolts: Double) {
        left.set(leftVolts)
        right.set(rightVolts)
    }

    override fun setSpeed(leftRPM: Double, leftFFVolts: Double, rightRPM: Double, rightFFVolts: Double) {
        leftPid.setReference(
            leftRPM * gearRatio,
            ControlType.kVelocity,
            0, // PID slot
            leftFFVolts,
            ArbFFUnits.kVoltage
        )
        rightPid.setReference(
            rightRPM * gearRatio,
            ControlType.kVelocity,
            0, // PID slot
            rightFFVolts,
            ArbFFUnits.kVoltage
        )
    }

    override fun setPID(p: Double, i: Double, d: Double) {
        leftPid.setP(p, 0)
        leftPid.setI(i, 0)
        leftPid.setD(d, 0)
        leftPid.setFF(0.0, 0)
        rightPid.setP(p, 0)
        rightPid.setI(i, 0)
        rightPid.setD(d, 0)
        rightPid.setFF(0.0, 0)
    }

    override fun stop() {
        left.stopMotor()
        right.stopMotor()
    }
}