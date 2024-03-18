package org.team9432.robot.subsystems.drivetrain

import edu.wpi.first.math.MathUtil
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.system.plant.DCMotor
import edu.wpi.first.wpilibj.simulation.DCMotorSim
import org.team9432.LOOP_PERIOD_SECS
import org.team9432.lib.constants.SwerveConstants.MK4I_L3_DRIVE_REDUCTION
import org.team9432.lib.constants.SwerveConstants.MK4I_STEER_REDUCTION
import org.team9432.robot.subsystems.drivetrain.ModuleIO.ModuleIOInputs
import kotlin.math.abs

class ModuleIOSim(override val module: ModuleIO.Module): ModuleIO {
    private val driveSim = DCMotorSim(DCMotor.getNeoVortex(1), MK4I_L3_DRIVE_REDUCTION, 0.025)
    private val steerSim = DCMotorSim(DCMotor.getNEO(1), MK4I_STEER_REDUCTION, 0.004096955)

    private val steerAbsoluteInitPosition = Rotation2d(Math.random() * 2.0 * Math.PI)

    private var driveAppliedVolts = 0.0
    private var steerAppliedVolts = 0.0

    override fun updateInputs(inputs: ModuleIOInputs) {
        driveSim.update(LOOP_PERIOD_SECS)
        steerSim.update(LOOP_PERIOD_SECS)

        inputs.drivePositionRad = driveSim.angularPositionRad
        inputs.driveVelocityRadPerSec = driveSim.angularVelocityRadPerSec
        inputs.driveAppliedVolts = driveAppliedVolts
        inputs.driveCurrentAmps = abs(driveSim.currentDrawAmps)

        inputs.steerAbsolutePosition = Rotation2d(steerSim.angularPositionRad).plus(steerAbsoluteInitPosition)
        inputs.steerPosition = Rotation2d(steerSim.angularPositionRad)
        inputs.steerVelocityRadPerSec = steerSim.angularVelocityRadPerSec
        inputs.steerAppliedVolts = steerAppliedVolts
        inputs.steerCurrentAmps = abs(steerSim.currentDrawAmps)
    }

    override fun setDriveVoltage(volts: Double) {
        driveAppliedVolts = MathUtil.clamp(volts, -12.0, 12.0)
        driveSim.setInputVoltage(driveAppliedVolts)
    }

    override fun setSteerVoltage(volts: Double) {
        steerAppliedVolts = MathUtil.clamp(volts, -12.0, 12.0)
        steerSim.setInputVoltage(steerAppliedVolts)
    }
}
