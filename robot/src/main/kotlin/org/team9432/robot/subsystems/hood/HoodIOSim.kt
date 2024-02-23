package org.team9432.robot.subsystems.hood

import edu.wpi.first.math.MathUtil
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.system.plant.DCMotor
import edu.wpi.first.wpilibj.simulation.DCMotorSim
import org.team9432.LOOP_PERIOD_SECS
import kotlin.math.abs

class HoodIOSim: HoodIO {
    private val motorToHoodRatio = 2.0 * (150 / 15)

    private val sim = DCMotorSim(DCMotor.getNEO(1), motorToHoodRatio, 0.01507)
    private val pid = PIDController(0.0, 0.0, 0.0)

    private val encoderInitPosition = Rotation2d(Math.random() * 2.0 * Math.PI)

    private var appliedVolts = 0.0
    private var ffVolts = 0.0
    private var isClosedLoop = false
    private var relativeOffset: Rotation2d? = null

    override fun updateInputs(inputs: HoodIO.HoodIOInputs) {
        if (isClosedLoop) {
            appliedVolts = MathUtil.clamp(pid.calculate(sim.angularPositionRad) + ffVolts, -12.0, 12.0)
            sim.setInputVoltage(appliedVolts)
        }

        sim.update(LOOP_PERIOD_SECS)

        inputs.absoluteAngle = Rotation2d(sim.angularPositionRad).plus(encoderInitPosition)
        inputs.relativeAngle = Rotation2d(sim.angularPositionRad)
        inputs.velocityDegPerSec = sim.angularVelocityRPM / 360
        inputs.appliedVolts = appliedVolts
        inputs.currentAmps = abs(sim.currentDrawAmps)

        // On first cycle, reset relative turn encoder
        // Wait until absolute angle is nonzero in case it wasn't initialized yet
        if (relativeOffset == null && inputs.absoluteAngle.radians != 0.0) {
            relativeOffset = inputs.absoluteAngle.minus(inputs.relativeAngle)
        }
    }

    override fun setVoltage(volts: Double) {
        isClosedLoop = false
        appliedVolts = MathUtil.clamp(volts, -12.0, 12.0)
        sim.setInputVoltage(appliedVolts)
    }

    override fun setAngle(angle: Rotation2d, feedforwardVolts: Double) {
        isClosedLoop = true
        pid.setpoint = angle.minus(relativeOffset ?: Rotation2d()).radians
        ffVolts = feedforwardVolts
    }

    override fun setPID(p: Double, i: Double, d: Double) {
        pid.setPID(p, i, d)
    }

    override fun stop() {
        setVoltage(0.0)
    }
}