package org.team9432.robot.subsystems.intake

import edu.wpi.first.math.MathUtil
import edu.wpi.first.math.system.plant.DCMotor
import edu.wpi.first.wpilibj.simulation.DCMotorSim
import org.team9432.LOOP_PERIOD_SECS
import kotlin.math.abs

class IntakeSideIOSim(override val intakeSide: IntakeSideIO.IntakeSide): IntakeSideIO {
    private val sim = DCMotorSim(DCMotor.getNEO(1), 2.0, 0.003)

    private var appliedVolts = 0.0

    override fun updateInputs(inputs: IntakeSideIO.IntakeSideIOInputs) {
        sim.update(LOOP_PERIOD_SECS)

        inputs.velocityRPM = sim.angularVelocityRPM
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