package org.team9432.robot.subsystems.drivetrain

import com.revrobotics.CANSparkBase
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.math.controller.SimpleMotorFeedforward
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.kinematics.SwerveModulePosition
import edu.wpi.first.math.kinematics.SwerveModuleState
import edu.wpi.first.math.util.Units
import edu.wpi.first.wpilibj.DriverStation
import org.littletonrobotics.junction.Logger
import org.team9432.lib.State
import org.team9432.lib.State.Mode.*
import org.team9432.lib.constants.SwerveConstants.MK4I_DRIVE_WHEEL_RADIUS
import org.team9432.lib.constants.SwerveConstants.MK4I_L2_DRIVE_REDUCTION
import org.team9432.lib.constants.SwerveConstants.MK4I_L3_DRIVE_REDUCTION
import org.team9432.lib.constants.SwerveConstants.MK4I_STEER_REDUCTION
import org.team9432.lib.wrappers.Spark
import org.team9432.lib.wrappers.cancoder.LoggedCancoder
import org.team9432.lib.wrappers.neo.LoggedNeo
import org.team9432.lib.wrappers.neo.LoggedNeoIO
import org.team9432.robot.oi.switches.DSSwitches
import kotlin.math.cos

class Module(private val module: ModuleConfig) {
    private val drive = LoggedNeo(getDriveConfig())
    private val steer = LoggedNeo(getSteerConfig())
    private val cancoder = LoggedCancoder(getCancoderConfig())

    private val driveFeedforward: SimpleMotorFeedforward
    private val driveFeedback: PIDController
    private val steerFeedback: PIDController

    private var angleSetpoint: Rotation2d? = null // Setpoint for closed loop control, null for open loop
    private var speedSetpoint: Double? = null // Setpoint for closed loop control, null for open loop

    private var steerRelativeOffset: Rotation2d? = null // Relative + Offset = Absolute

    private fun Double.adjustRatio() = (this / MK4I_L2_DRIVE_REDUCTION) * MK4I_L3_DRIVE_REDUCTION

    private var isBrakeMode: Boolean? = null

    private var driveInputs: LoggedNeoIO.NEOIOInputs = LoggedNeoIO.NEOIOInputs()
    private var steerInputs: LoggedNeoIO.NEOIOInputs = LoggedNeoIO.NEOIOInputs()

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

        setBrakeMode(true)
    }

    fun periodic() {
        steerInputs = steer.updateAndRecordInputs()
        driveInputs = drive.updateAndRecordInputs()
        val cancoderInputs = cancoder.updateAndRecordInputs()

        if (DSSwitches.drivetrainDisabled) {
            drive.stop()
            steer.stop()

            setBrakeMode(false)
            return
        } else setBrakeMode(true)

        val steerAbsolutePosition = cancoderInputs.position

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

        if (DriverStation.isTestEnabled()) {
            Logger.recordOutput("Drive/${module.name}_Module/DrivePositionRadians", driveInputs.angle.radians)
        }
    }

    private fun getAngle(): Rotation2d {
        return steerInputs.angle.plus(steerRelativeOffset ?: Rotation2d())
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
        if (isBrakeMode != enabled) {
            isBrakeMode = enabled
            drive.setBrakeMode(enabled)
            steer.setBrakeMode(enabled)
        }
    }

    /** Set the module to run open loop drive control at the specified voltage while using the angle controller to lock the module in place. Used for sysid characterization. */
    fun runCharacterization(volts: Double) {
        angleSetpoint = Rotation2d()
        speedSetpoint = null
        drive.setVoltage(volts)
    }

    val positionMeters get() = driveInputs.angle.radians * Units.inchesToMeters(MK4I_DRIVE_WHEEL_RADIUS)
    val velocityMetersPerSec get() = driveInputs.velocityRadPerSec * Units.inchesToMeters(MK4I_DRIVE_WHEEL_RADIUS)
    val position get() = SwerveModulePosition(positionMeters, getAngle())
    val state get() = SwerveModuleState(velocityMetersPerSec, getAngle())

    private fun getDriveConfig(): LoggedNeo.Config {
        return LoggedNeo.Config(
            canID = module.driveID,
            motorType = Spark.MotorType.VORTEX,
            deviceName = "${module.name} Drive Motor",
            sparkConfig = Spark.Config(
                inverted = module.driveInverted,
                idleMode = CANSparkBase.IdleMode.kBrake,
                stallCurrentLimit = 45
            ),
            logName = "Drive/${module.name}Module",
            gearRatio = MK4I_L3_DRIVE_REDUCTION,
            simJkgMetersSquared = 0.025,
            additionalQualifier = "Drive"
        )
    }

    private fun getSteerConfig(): LoggedNeo.Config {
        return LoggedNeo.Config(
            canID = module.steerID,
            motorType = Spark.MotorType.NEO,
            deviceName = "${module.name} Steer Motor",
            sparkConfig = Spark.Config(
                inverted = module.steerInverted,
                idleMode = CANSparkBase.IdleMode.kBrake,
                stallCurrentLimit = 30
            ),
            logName = "Drive/${module.name}Module",
            gearRatio = MK4I_STEER_REDUCTION,
            simJkgMetersSquared = 0.004096955,
            additionalQualifier = "Steer"
        )
    }

    private fun getCancoderConfig(): LoggedCancoder.Config {
        return LoggedCancoder.Config(
            canID = module.encoderID,
            deviceName = "${module.name} Cancoder",
            logName = "Drive/${module.name}Module",
            encoderOffset = module.encoderOffset,
            simPositionSupplier = { steerInputs.angle },
            additionalQualifier = "Cancoder"
        )
    }
}