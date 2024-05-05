package org.team9432.lib.wrappers.neo

import edu.wpi.first.math.MathUtil
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.system.plant.DCMotor
import edu.wpi.first.math.util.Units
import edu.wpi.first.wpilibj.simulation.DCMotorSim
import org.littletonrobotics.junction.LOOP_PERIOD
import org.team9432.lib.unit.inSeconds
import org.team9432.lib.wrappers.Spark
import kotlin.math.abs

class LoggedNeoIOSim(config: LoggedNeo.Config): LoggedNeoIO {
    private val sim = DCMotorSim(
        when (config.motorType) {
            Spark.MotorType.NEO -> DCMotor.getNEO(1)
            Spark.MotorType.VORTEX -> DCMotor.getNeoVortex(1)
        }, config.gearRatio, config.simJkgMetersSquared
    )

    private var appliedVolts = 0.0

    override fun updateInputs(inputs: LoggedNeoIO.NEOIOInputs) {
        sim.update(LOOP_PERIOD.inSeconds)

        inputs.angle = Rotation2d(sim.angularPositionRad)
        inputs.velocityRadPerSec = Units.rotationsPerMinuteToRadiansPerSecond(sim.angularVelocityRPM)
        inputs.appliedVolts = appliedVolts
        inputs.currentAmps = abs(sim.currentDrawAmps)
    }

    override fun setVoltage(volts: Double) {
        appliedVolts = MathUtil.clamp(volts, -12.0, 12.0)
        sim.setInputVoltage(appliedVolts)
    }

    override fun stop() = setVoltage(0.0)
}