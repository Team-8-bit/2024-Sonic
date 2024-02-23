package org.team9432.robot.subsystems.shooter

import edu.wpi.first.math.MathUtil
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.math.system.plant.DCMotor
import edu.wpi.first.wpilibj.simulation.DCMotorSim
import org.team9432.LOOP_PERIOD_SECS
import kotlin.math.abs

class ShooterIOSim: ShooterIO {
    private val gearRatio = 0.5

    private val leftSim = DCMotorSim(DCMotor.getNeoVortex(1), gearRatio, 0.003)
    private val rightSim = DCMotorSim(DCMotor.getNeoVortex(1), gearRatio, 0.003)

    private val leftPid = PIDController(0.0, 0.0, 0.0)
    private val rightPid = PIDController(0.0, 0.0, 0.0)

    private var leftAppliedVolts = 0.0
    private var rightAppliedVolts = 0.0
    private var leftFFVolts = 0.0
    private var rightFFVolts = 0.0
    private var isClosedLoop = false

    override fun updateInputs(inputs: ShooterIO.ShooterIOInputs) {
        if (isClosedLoop) {
            leftAppliedVolts = MathUtil.clamp(leftPid.calculate(leftSim.angularVelocityRPM) + leftFFVolts, -12.0, 12.0)
            leftSim.setInputVoltage(leftAppliedVolts)

            rightAppliedVolts = MathUtil.clamp(rightPid.calculate(rightSim.angularVelocityRPM) + rightFFVolts, -12.0, 12.0)
            rightSim.setInputVoltage(leftAppliedVolts)
        }

        leftSim.update(LOOP_PERIOD_SECS)
        rightSim.update(LOOP_PERIOD_SECS)

        inputs.leftVelocityRPM = leftSim.angularVelocityRPM
        inputs.leftAppliedVolts = leftAppliedVolts
        inputs.leftCurrentAmps = abs(leftSim.currentDrawAmps)
        inputs.rightVelocityRPM = rightSim.angularVelocityRPM
        inputs.rightAppliedVolts = rightAppliedVolts
        inputs.rightCurrentAmps = abs(rightSim.currentDrawAmps)
    }

    override fun setVoltage(leftVolts: Double, rightVolts: Double) {
        isClosedLoop = false

        leftAppliedVolts = MathUtil.clamp(leftVolts, -12.0, 12.0)
        leftSim.setInputVoltage(leftAppliedVolts)

        rightAppliedVolts = MathUtil.clamp(rightVolts, -12.0, 12.0)
        rightSim.setInputVoltage(rightAppliedVolts)
    }

    override fun setSpeed(leftRPM: Double, leftFFVolts: Double, rightRPM: Double, rightFFVolts: Double) {
        isClosedLoop = true

        leftPid.setpoint = leftRPM
        this.leftFFVolts = leftFFVolts

        rightPid.setpoint = rightRPM
        this.rightFFVolts = rightFFVolts
    }

    override fun setPID(p: Double, i: Double, d: Double) {
        // It should be the same constants for both
        leftPid.setPID(p, i, d)
        rightPid.setPID(p, i, d)
    }

    override fun stop() {
        setVoltage(0.0, 0.0)
    }
}
