package org.team9432.robot.subsystems.drivetrain

import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.math.controller.SimpleMotorFeedforward
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.kinematics.SwerveModulePosition
import edu.wpi.first.math.kinematics.SwerveModuleState
import edu.wpi.first.math.util.Units
import org.littletonrobotics.junction.Logger
import org.team9432.Robot
import org.team9432.Robot.Mode.*
import org.team9432.lib.constants.SwerveConstants.MK4I_DRIVE_WHEEL_RADIUS
import org.team9432.lib.util.SwerveUtil
import kotlin.math.cos


class Module(module: ModuleIO.Module) {
    private val io: ModuleIO
    private val inputs = LoggedModuleIOInputs()

    private val driveFeedforward: SimpleMotorFeedforward
    private val driveFeedback: PIDController
    private val steerFeedback: PIDController

    private var angleSetpoint: Rotation2d? = null // Setpoint for closed loop control, null for open loop
    private var speedSetpoint: Double? = null // Setpoint for closed loop control, null for open loop

    private var steerRelativeOffset: Rotation2d? = null // Relative + Offset = Absolute

    init {
        when (Robot.mode) {
            REAL, REPLAY -> {
                io = ModuleIONEO(module)
                driveFeedforward = SimpleMotorFeedforward(0.1, 0.13)
                driveFeedback = PIDController(0.05, 0.0, 0.0)
                steerFeedback = PIDController(7.0, 0.0, 0.0)
            }

            SIM -> {
                io = ModuleIOSim(module)
                driveFeedforward = SimpleMotorFeedforward(0.0, 0.13)
                driveFeedback = PIDController(0.1, 0.0, 0.0)
                steerFeedback = PIDController(10.0, 0.0, 0.0)
            }
        }

        steerFeedback.enableContinuousInput(-Math.PI, Math.PI);
        setBrakeMode(true)
    }

    fun periodic() {
        io.updateInputs(inputs)
        Logger.processInputs("Drive/${io.module.name}_Module", inputs)

        // On first cycle, reset relative turn encoder
        // Wait until absolute angle is nonzero in case it wasn't initialized yet
        if (steerRelativeOffset == null && inputs.steerAbsolutePosition.radians != 0.0) {
            steerRelativeOffset = inputs.steerAbsolutePosition.minus(inputs.steerPosition)
        }

        // Run closed loop turn control
        if (angleSetpoint != null) {
            io.setSteerVoltage(steerFeedback.calculate(getAngle().radians, angleSetpoint!!.radians))

            // Run closed loop drive control
            // Only allowed if closed loop turn control is running
            if (speedSetpoint != null) {
                // Scale velocity based on turn error
                //
                // When the error is 90°, the velocity setpoint should be 0. As the wheel turns
                // towards the setpoint, its velocity should increase. This is achieved by
                // taking the component of the velocity in the direction of the setpoint.
                val adjustSpeedSetpoint = speedSetpoint!! * cos(steerFeedback.positionError)

                // Run drive controller
                val velocityRadPerSec = adjustSpeedSetpoint / Units.inchesToMeters(MK4I_DRIVE_WHEEL_RADIUS)
                io.setDriveVoltage(driveFeedforward.calculate(velocityRadPerSec) + driveFeedback.calculate(inputs.driveVelocityRadPerSec, velocityRadPerSec))
            }
        }

        Logger.recordOutput("Drive/${io.module.name}_Module/AbsoluteAngleDegrees", inputs.steerAbsolutePosition.degrees)
    }

    private fun getAngle(): Rotation2d {
        return inputs.steerPosition.plus(steerRelativeOffset ?: Rotation2d())
    }

    fun runSetpoint(state: SwerveModuleState): SwerveModuleState {
        val optimizedState = SwerveModuleState.optimize(state, getAngle())
        angleSetpoint = optimizedState.angle
        speedSetpoint = optimizedState.speedMetersPerSecond
        return optimizedState
    }

    fun stop() {
        io.setSteerVoltage(0.0)
        io.setDriveVoltage(0.0)

        // Disable closed loop control for turn and drive
        angleSetpoint = null
        speedSetpoint = null
    }

    fun setBrakeMode(enabled: Boolean) = io.setBrakeMode(enabled)

    val positionMeters get() = inputs.drivePositionRad * Units.inchesToMeters(MK4I_DRIVE_WHEEL_RADIUS)
    val velocityMetersPerSec get() = inputs.driveVelocityRadPerSec * Units.inchesToMeters(MK4I_DRIVE_WHEEL_RADIUS)
    val position get() = SwerveModulePosition(positionMeters, getAngle())
    val state get() = SwerveModuleState(velocityMetersPerSec, getAngle())
}