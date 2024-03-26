package org.team9432.lib.motors.neo

import edu.wpi.first.math.MathUtil
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.system.plant.DCMotor
import edu.wpi.first.math.util.Units
import edu.wpi.first.wpilibj.simulation.DCMotorSim
import org.team9432.LOOP_PERIOD_SECS
import kotlin.math.abs

class NeoIOSim(gearRatio: Double, jkgMetersSquared: Double): NeoIO {
    private val sim = DCMotorSim(DCMotor.getNEO(1), gearRatio, jkgMetersSquared)

    private var appliedVolts = 0.0

    override fun updateInputs(inputs: NeoIO.NEOIOInputs) {
        sim.update(LOOP_PERIOD_SECS)

        inputs.angle = Rotation2d(sim.angularPositionRad)
        inputs.velocityRadPerSec = Units.rotationsPerMinuteToRadiansPerSecond(sim.angularVelocityRPM)
        inputs.appliedVolts = appliedVolts
        inputs.currentAmps = abs(sim.currentDrawAmps)
    }

    override fun setVoltage(volts: Double) {
        appliedVolts = MathUtil.clamp(volts, -12.0, 12.0)
        sim.setInputVoltage(appliedVolts)
    }

    override fun stop() {
        setVoltage(0.0)
    }
}