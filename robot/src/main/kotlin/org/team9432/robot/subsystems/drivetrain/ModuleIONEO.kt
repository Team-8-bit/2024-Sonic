package org.team9432.robot.subsystems.drivetrain

import com.ctre.phoenix6.configs.CANcoderConfiguration
import com.ctre.phoenix6.hardware.CANcoder
import com.ctre.phoenix6.signals.AbsoluteSensorRangeValue
import com.revrobotics.CANSparkBase
import com.revrobotics.CANSparkBase.*
import edu.wpi.first.math.kinematics.SwerveModuleState
import edu.wpi.first.math.util.Units
import org.team9432.lib.drivers.motors.KSparkMAX
import org.team9432.lib.util.MotorConversions
import org.team9432.lib.util.SwerveUtil
import org.team9432.robot.DrivetrainConstants
import org.team9432.robot.DrivetrainConstants.DRIVE_WHEEL_CIRCUMFERENCE
import org.team9432.robot.DrivetrainConstants.DRIVE_WHEEL_DIAMETER
import org.team9432.robot.DrivetrainConstants.MK4I_L2_DRIVE_REDUCTION
import org.team9432.robot.DrivetrainConstants.MK4I_L2_STEER_REDUCTION
import org.team9432.robot.subsystems.drivetrain.ModuleIO.ModuleIOInputs

class ModuleIONEO(override val module: ModuleIO.Module): ModuleIO {
    private val drive = KSparkMAX(module.driveID)
    private val steer = KSparkMAX(module.steerID)
    private val driveEncoder = drive.encoder
    private val steerEncoder = steer.encoder
    private val drivePID = drive.pidController
    private val steerPID = steer.pidController
    private val cancoder = CANcoder(module.encoderID)

    private val angleOffset = DrivetrainConstants.MODULE_OFFSETS[module]!!
    private var currentTarget = SwerveModuleState()

    override var disabled = false
        set(disabled) {
            if (disabled) {
                drive.disable()
                steer.disable()
            }
            field = disabled
        }

    init {
        drive.restoreFactoryDefaults()
        drive.setSmartCurrentLimit(20)
        drive.openLoopRampRate = 1.0
        drive.inverted = true
        drivePID.p = 0.0000628319270
        drivePID.i = 0.0
        drivePID.d = 0.0001
        drivePID.setOutputRange(-1.0, 1.0)

        steer.restoreFactoryDefaults()
        steer.setSmartCurrentLimit(20)
        steer.openLoopRampRate = 1.0
        steer.inverted = true
        steerPID.p = 0.3
        steerPID.i = 0.0
        steerPID.d = 0.0
        steerPID.setOutputRange(-1.0, 1.0)

        val cancoderConfig = CANcoderConfiguration()
        cancoderConfig.MagnetSensor.AbsoluteSensorRange = AbsoluteSensorRangeValue.Unsigned_0To1
        cancoder.configurator.apply(cancoderConfig)
    }

    override fun setState(state: SwerveModuleState) {
        currentTarget = SwerveUtil.optimize(state, MotorConversions.NEOToDegrees(steerEncoder.position, MK4I_L2_STEER_REDUCTION))
        drivePID.setReference(MotorConversions.MPSToNEO(currentTarget.speedMetersPerSecond, Units.inchesToMeters(DRIVE_WHEEL_DIAMETER), MK4I_L2_DRIVE_REDUCTION), ControlType.kVelocity)
        val rotations = MotorConversions.degreesToNEO(currentTarget.angle.degrees, MK4I_L2_STEER_REDUCTION)
        steerPID.setReference(rotations, ControlType.kPosition)
    }

    override fun updateInputs(inputs: ModuleIOInputs) {
        inputs.positionMeters = MotorConversions.NEOToRotations(driveEncoder.position, MK4I_L2_DRIVE_REDUCTION) * Units.inchesToMeters(DRIVE_WHEEL_CIRCUMFERENCE)
        inputs.speedMetersPerSecond = MotorConversions.NEOToMPS(driveEncoder.velocity, Units.inchesToMeters(DRIVE_WHEEL_CIRCUMFERENCE), MK4I_L2_DRIVE_REDUCTION)
        inputs.angle = MotorConversions.NEOToDegrees(steerEncoder.position, MK4I_L2_STEER_REDUCTION)
        inputs.targetAngle = currentTarget.angle.degrees
        inputs.targetSpeed = currentTarget.speedMetersPerSecond
    }

    override fun updateIntegratedEncoder() {
        steerEncoder.position = MotorConversions.degreesToNEO((cancoder.absolutePosition.value * 360) - angleOffset, MK4I_L2_STEER_REDUCTION)
    }

    override fun setBrakeMode(enabled: Boolean) {
        drive.idleMode = if (enabled) IdleMode.kBrake else IdleMode.kCoast
        steer.idleMode = if (enabled) IdleMode.kBrake else IdleMode.kCoast
    }
}
