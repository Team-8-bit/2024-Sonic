package org.team9432.lib.motors.neo

import edu.wpi.first.math.MathUtil
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.system.plant.DCMotor
import edu.wpi.first.math.util.Units
import edu.wpi.first.wpilibj.simulation.DCMotorSim
import org.team9432.LOOP_PERIOD_SECS
import kotlin.math.abs

class NeoIOSim(config: NEO.Config): NeoIO {
    private val sim = DCMotorSim(DCMotor.getNEO(1), config.gearRatio, config.simJkgMetersSquared)
    private val pid = PIDController(0.0, 0.0, 0.0)

    private var appliedVolts = 0.0
    private var isClosedLoop = false

    override fun updateInputs(inputs: NeoIO.NEOIOInputs) {
        if (isClosedLoop) {
            appliedVolts = MathUtil.clamp(pid.calculate(sim.angularPositionRad), -12.0, 12.0)
            sim.setInputVoltage(appliedVolts)
        }

        sim.update(LOOP_PERIOD_SECS)

        inputs.angle = Rotation2d(sim.angularPositionRad)
        inputs.velocityRadPerSec = Units.rotationsPerMinuteToRadiansPerSecond(sim.angularVelocityRPM)
        inputs.appliedVolts = appliedVolts
        inputs.currentAmps = abs(sim.currentDrawAmps)
    }

    override fun setVoltage(volts: Double) {
        isClosedLoop = false
        appliedVolts = MathUtil.clamp(volts, -12.0, 12.0)
        sim.setInputVoltage(appliedVolts)
    }

    override fun setAngle(angle: Rotation2d) {
        isClosedLoop = true
        pid.setpoint = angle.radians
    }

    override fun setPID(p: Double, i: Double, d: Double) = pid.setPID(p, i, d)
    override fun stop() = setVoltage(0.0)
}