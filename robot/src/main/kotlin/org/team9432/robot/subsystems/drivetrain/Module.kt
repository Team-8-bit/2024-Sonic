package org.team9432.robot.subsystems.drivetrain

import com.ctre.phoenix6.BaseStatusSignal
import com.ctre.phoenix6.StatusSignal
import com.ctre.phoenix6.configs.CANcoderConfiguration
import com.ctre.phoenix6.hardware.CANcoder
import com.ctre.phoenix6.signals.AbsoluteSensorRangeValue
import com.revrobotics.CANSparkBase
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.math.controller.SimpleMotorFeedforward
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.kinematics.SwerveModulePosition
import edu.wpi.first.math.kinematics.SwerveModuleState
import edu.wpi.first.math.util.Units
import org.littletonrobotics.junction.Logger
import org.team9432.lib.State
import org.team9432.lib.State.Mode.*
import org.team9432.lib.constants.SwerveConstants.MK4I_DRIVE_WHEEL_RADIUS
import org.team9432.lib.constants.SwerveConstants.MK4I_L2_DRIVE_REDUCTION
import org.team9432.lib.constants.SwerveConstants.MK4I_L3_DRIVE_REDUCTION
import org.team9432.lib.constants.SwerveConstants.MK4I_STEER_REDUCTION
import org.team9432.lib.motors.neo.Neo
import org.team9432.lib.wrappers.Spark
import kotlin.math.cos

class Module(private val module: ModuleConfig) {
    private val drive = Neo(getDriveConfig())
    private val steer = Neo(getSteerConfig())
    private val cancoder = CANcoder(module.encoderID)

    private val driveFeedforward: SimpleMotorFeedforward
    private val driveFeedback: PIDController
    private val steerFeedback: PIDController

    private var angleSetpoint: Rotation2d? = null // Setpoint for closed loop control, null for open loop
    private var speedSetpoint: Double? = null // Setpoint for closed loop control, null for open loop

    private var steerRelativeOffset: Rotation2d? = null // Relative + Offset = Absolute

    private fun Double.adjustRatio() = (this / MK4I_L2_DRIVE_REDUCTION) * MK4I_L3_DRIVE_REDUCTION

    private val steerAbsolutePositionSignal: StatusSignal<Double>

    init {
        when (State.mode) {
            REAL, REPLAY -> {
                driveFeedforward = SimpleMotorFeedforward(0.1.adjustRatio(), 0.13.adjustRatio())
                driveFeedback = PIDController(0.05.adjustRatio(), 0.0, 0.0)
                steerFeedback = PIDController(7.0, 0.0, 0.0)
            }

            SIM -> {
                driveFeedforward = SimpleMotorFeedforward(0.0, 0.13)
                driveFeedback = PIDController(0.1, 0.0, 0.0)
                steerFeedback = PIDController(10.0, 0.0, 0.0)
            }
        }

        steerFeedback.enableContinuousInput(-Math.PI, Math.PI)

        val cancoderConfig = CANcoderConfiguration()
        cancoderConfig.MagnetSensor.AbsoluteSensorRange = AbsoluteSensorRangeValue.Unsigned_0To1
        cancoder.configurator.apply(cancoderConfig)

        steerAbsolutePositionSignal = cancoder.absolutePosition
        BaseStatusSignal.setUpdateFrequencyForAll(50.0, steerAbsolutePositionSignal)
        cancoder.optimizeBusUtilization()

        setBrakeMode(true)
    }

    fun periodic() {
        BaseStatusSignal.refreshAll(steerAbsolutePositionSignal)

        val steerInputs = steer.inputs
        val driveInputs = drive.inputs

        val steerAbsolutePosition = Rotation2d.fromRotations(steerAbsolutePositionSignal.valueAsDouble).minus(module.encoderOffset)

        // On first cycle, reset relative turn encoder
        // Wait until absolute angle is nonzero in case it wasn't initialized yet
        if (steerRelativeOffset == null && steerAbsolutePosition.radians != 0.0) {
            steerRelativeOffset = steerAbsolutePosition.minus(steerInputs.angle)
        }

        // Run closed loop turn control
        if (angleSetpoint != null) {
            steer.setVoltage(steerFeedback.calculate(getAngle().radians, angleSetpoint!!.radians))

            // Run closed loop drive control
            // Only allowed if closed loop turn control is running
            if (speedSetpoint != null) {
                // Scale velocity based on turn error

                // When the error is 90°, the velocity setpoint should be 0. As the wheel turns
                // towards the setpoint, its velocity should increase. This is achieved by
                // taking the component of the velocity in the direction of the setpoint.
                val adjustSpeedSetpoint = speedSetpoint!! * cos(steerFeedback.positionError)

                // Run drive controller
                val velocityRadPerSec = adjustSpeedSetpoint / Units.inchesToMeters(MK4I_DRIVE_WHEEL_RADIUS)
                drive.setVoltage(driveFeedforward.calculate(velocityRadPerSec) + driveFeedback.calculate(driveInputs.velocityRadPerSec, velocityRadPerSec))
            }
        }

        Logger.recordOutput("Drive/${module.name}_Module/AbsoluteAngleDegrees", steerAbsolutePosition.degrees)
    }

    private fun getAngle(): Rotation2d {
        return steer.inputs.angle.plus(steerRelativeOffset ?: Rotation2d())
    }

    fun runSetpoint(state: SwerveModuleState): SwerveModuleState {
        val optimizedState = SwerveModuleState.optimize(state, getAngle())
        angleSetpoint = optimizedState.angle
        speedSetpoint = optimizedState.speedMetersPerSecond
        return optimizedState
    }

    fun stop() {
        steer.stop()
        drive.stop()

        // Disable closed loop control for turn and drive
        angleSetpoint = null
        speedSetpoint = null
    }

    fun setBrakeMode(enabled: Boolean) {
        drive.setBrakeMode(enabled)
        steer.setBrakeMode(enabled)
    }

    val positionMeters get() = drive.inputs.angle.radians * Units.inchesToMeters(MK4I_DRIVE_WHEEL_RADIUS)
    val velocityMetersPerSec get() = drive.inputs.velocityRadPerSec * Units.inchesToMeters(MK4I_DRIVE_WHEEL_RADIUS)
    val position get() = SwerveModulePosition(positionMeters, getAngle())
    val state get() = SwerveModuleState(velocityMetersPerSec, getAngle())

    private fun getDriveConfig(): Neo.Config {
        return Neo.Config(
            canID = module.driveID,
            motorType = Spark.MotorType.VORTEX,
            name = "${module.name} Drive Motor",
            sparkConfig = Spark.Config(
                inverted = module.driveInverted,
                idleMode = CANSparkBase.IdleMode.kBrake,
                smartCurrentLimit = 50
            ),
            logName = "Drive/${module.name}ModuleDrive",
            gearRatio = MK4I_L3_DRIVE_REDUCTION,
            simJkgMetersSquared = 0.025
        )
    }

    private fun getSteerConfig(): Neo.Config {
        return Neo.Config(
            canID = module.steerID,
            motorType = Spark.MotorType.NEO,
            name = "${module.name} Steer Motor",
            sparkConfig = Spark.Config(
                inverted = module.steerInverted,
                idleMode = CANSparkBase.IdleMode.kBrake,
                smartCurrentLimit = 30
            ),
            logName = "Drive/${module.name}ModuleSteer",
            gearRatio = MK4I_STEER_REDUCTION,
            simJkgMetersSquared = 0.004096955
        )
    }
}