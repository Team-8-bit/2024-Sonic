package org.team9432.lib.logged.neo

import edu.wpi.first.math.MathUtil
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.system.plant.DCMotor
import edu.wpi.first.math.util.Units
import edu.wpi.first.wpilibj.simulation.DCMotorSim
import org.team9432.LOOP_PERIOD_SECS
import org.team9432.lib.wrappers.Spark
import kotlin.math.abs

class LoggedNeoIOSim(config: LoggedNeo.Config): LoggedNeoIO {
    private val sim = DCMotorSim(
        when (config.motorType) {
            Spark.MotorType.NEO -> DCMotor.getNEO(1)
            Spark.MotorType.VORTEX -> DCMotor.getNeoVortex(1)
        }, config.gearRatio, config.simJkgMetersSquared
    )

    private var controlMode = LoggedNeo.ControlMode.VOLTAGE

    private val pid = PIDController(0.0, 0.0, 0.0)

    private var appliedVolts = 0.0

    override fun updateInputs(inputs: LoggedNeoIO.NEOIOInputs) {
        when (controlMode) {
            LoggedNeo.ControlMode.VOLTAGE -> {}
            LoggedNeo.ControlMode.POSITION -> {
                appliedVolts = MathUtil.clamp(pid.calculate(sim.angularPositionRad), -12.0, 12.0)
                sim.setInputVoltage(appliedVolts)
            }
            LoggedNeo.ControlMode.VELOCITY -> {
                appliedVolts = MathUtil.clamp(pid.calculate(sim.angularVelocityRPM), -12.0, 12.0)
                sim.setInputVoltage(appliedVolts)
            }
        }

        sim.update(LOOP_PERIOD_SECS)

        inputs.angle = Rotation2d(sim.angularPositionRad)
        inputs.velocityRadPerSec = Units.rotationsPerMinuteToRadiansPerSecond(sim.angularVelocityRPM)
        inputs.appliedVolts = appliedVolts
        inputs.currentAmps = abs(sim.currentDrawAmps)
    }

    override fun setVoltage(volts: Double) {
        controlMode = LoggedNeo.ControlMode.VOLTAGE
        appliedVolts = MathUtil.clamp(volts, -12.0, 12.0)
        sim.setInputVoltage(appliedVolts)
    }

    override fun setAngle(angle: Rotation2d) {
        controlMode = LoggedNeo.ControlMode.POSITION
        pid.setpoint = angle.radians
    }

    override fun setSpeed(rpm: Int) {
        controlMode = LoggedNeo.ControlMode.VELOCITY
        pid.setpoint = rpm.toDouble()
    }

    override fun setPID(p: Double, i: Double, d: Double) = pid.setPID(p, i, d)
    override fun stop() = setVoltage(0.0)
}