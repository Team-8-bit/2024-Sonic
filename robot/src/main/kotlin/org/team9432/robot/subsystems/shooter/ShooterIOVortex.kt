package org.team9432.robot.subsystems.shooter

import com.revrobotics.CANSparkBase.*
import com.revrobotics.SparkPIDController.ArbFFUnits
import org.team9432.lib.drivers.motors.KSparkFlex
import org.team9432.robot.Ports

class ShooterIOVortex: ShooterIO {
    private val leader = KSparkFlex(Ports.Shooter.LEFT_SHOOTER_ID)
    private val follower = KSparkFlex(Ports.Shooter.RIGHT_SHOOTER_ID)

    private val encoder = leader.encoder
    private val pid = leader.pidController

    private val gearRatio = 0.5

    init {
        leader.restoreFactoryDefaults()
        follower.restoreFactoryDefaults()

        leader.inverted = false

        follower.follow(leader, true)

        leader.idleMode = IdleMode.kCoast
        follower.idleMode = IdleMode.kCoast

        leader.enableVoltageCompensation(12.0)
        follower.enableVoltageCompensation(12.0)

        leader.setSmartCurrentLimit(30)
        follower.setSmartCurrentLimit(30)

        leader.burnFlash()
        follower.burnFlash()
    }

    override fun updateInputs(inputs: ShooterIO.ShooterIOInputs) {
        inputs.velocityRPM = encoder.velocity / gearRatio
        inputs.appliedVolts = leader.appliedOutput * leader.busVoltage
        inputs.currentAmps = doubleArrayOf(leader.outputCurrent, follower.outputCurrent)
    }

    override fun setVoltage(volts: Double) = leader.setVoltage(volts)

    override fun setSpeed(rotationPerMinute: Double, feedforwardVolts: Double) {
        pid.setReference(
            rotationPerMinute * gearRatio,
            ControlType.kVelocity,
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

    override fun stop() = leader.stopMotor()
}