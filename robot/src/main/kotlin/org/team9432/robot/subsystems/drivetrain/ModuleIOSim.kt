package org.team9432.robot.subsystems.drivetrain

import edu.wpi.first.math.MathUtil
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.math.kinematics.SwerveModuleState
import edu.wpi.first.math.system.plant.DCMotor
import edu.wpi.first.math.util.Units
import edu.wpi.first.wpilibj.simulation.FlywheelSim
import org.team9432.Robot
import org.team9432.lib.util.RotationUtil
import org.team9432.lib.util.SwerveUtil
import org.team9432.robot.DrivetrainConstants.DRIVE_WHEEL_CIRCUMFERENCE
import org.team9432.robot.DrivetrainConstants.MK4I_L2_DRIVE_REDUCTION
import org.team9432.robot.DrivetrainConstants.MK4I_L2_STEER_REDUCTION

class ModuleIOSim(override val module: ModuleIO.Module): ModuleIO {
    private val driveSim = FlywheelSim(DCMotor.getNEO(1), MK4I_L2_DRIVE_REDUCTION, 0.025)
    private val steerSim = FlywheelSim(DCMotor.getNEO(1), MK4I_L2_STEER_REDUCTION, 0.004096955)
    private val drivePID = PIDController(20.0, 0.0, 0.003)
    private val steerPID = PIDController(0.1, 0.0, 0.1)

    private var currentAngle = 0.0
    private var currentTarget = SwerveModuleState()

    override var disabled = false

    init {
        drivePID.setTolerance(0.0)
        steerPID.setTolerance(0.0)
    }

    override fun setState(state: SwerveModuleState) {
        currentTarget = SwerveUtil.optimize(state, currentAngle)
        drivePID.setSetpoint(currentTarget.speedMetersPerSecond)
        steerPID.setSetpoint(currentTarget.angle.degrees)
    }

    override fun updateInputs(inputs: ModuleIO.ModuleIOInputs) {
        steerPID.calculate(inputs.angle)
        drivePID.calculate(inputs.speedMetersPerSecond)

        val driveVoltage = MathUtil.applyDeadband(drivePID.calculate(inputs.speedMetersPerSecond), 0.001)
        val steerVoltage = MathUtil.applyDeadband(steerPID.calculate(inputs.angle), 0.001)

        if (disabled) {
            driveSim.setInputVoltage(0.0)
            steerSim.setInputVoltage(0.0)
        } else {
            driveSim.setInputVoltage(driveVoltage)
            steerSim.setInputVoltage(steerVoltage)
        }

        driveSim.update(Robot.period)
        steerSim.update(Robot.period)

        currentAngle = RotationUtil.toSignedDegrees(inputs.angle)

        inputs.speedMetersPerSecond =
            Units.inchesToMeters(DRIVE_WHEEL_CIRCUMFERENCE) * (Math.toDegrees(driveSim.angularVelocityRadPerSec) / 360)
        inputs.positionMeters += inputs.speedMetersPerSecond * Robot.period
        inputs.angle += Math.toDegrees(steerSim.angularVelocityRadPerSec) * Robot.period
    }
}
