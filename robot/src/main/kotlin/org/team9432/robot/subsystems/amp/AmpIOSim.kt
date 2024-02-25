package org.team9432.robot.subsystems.amp

import edu.wpi.first.math.MathUtil
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.math.system.plant.DCMotor
import edu.wpi.first.wpilibj.simulation.DCMotorSim
import org.team9432.LOOP_PERIOD_SECS
import kotlin.math.abs

class AmpIOSim: AmpIO {
        private val sim = DCMotorSim(DCMotor.getNEO(1), 1.0, 0.003)

        private val pid = PIDController(0.0, 0.0, 0.0)

        private var appliedVolts = 0.0

        private var ffVolts = 0.0

        private var isClosedLoop = false

        override fun updateInputs(inputs: AmpIO.AmpIOInputs) {
            if (isClosedLoop) {
                appliedVolts = MathUtil.clamp(pid.calculate(sim.angularVelocityRPM) + ffVolts, -12.0, 12.0)
                sim.setInputVoltage(appliedVolts)
            }

            sim.update(LOOP_PERIOD_SECS)

            inputs.velocityRPM = sim.angularVelocityRPM
            inputs.appliedVolts = appliedVolts
            inputs.currentAmps = abs(sim.currentDrawAmps)
        }

        override fun setVoltage(volts: Double) {
            isClosedLoop = false

            appliedVolts = MathUtil.clamp(volts, -12.0, 12.0)
            sim.setInputVoltage(appliedVolts)
        }

        override fun setSpeed(rpm: Double, ffVolts: Double) {
            isClosedLoop = true

            pid.setpoint = rpm
            this.ffVolts = ffVolts
        }

        override fun setPID(p: Double, i: Double, d: Double) {
            pid.setPID(p, i, d)
        }

        override fun stop() {
            setVoltage(0.0)
        }
}