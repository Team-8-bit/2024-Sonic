package org.team9432.robot.subsystems.drivetrain

import com.ctre.phoenix6.BaseStatusSignal
import com.ctre.phoenix6.StatusSignal
import com.ctre.phoenix6.configs.CANcoderConfiguration
import com.ctre.phoenix6.hardware.CANcoder
import com.ctre.phoenix6.signals.AbsoluteSensorRangeValue
import com.revrobotics.CANSparkBase.IdleMode
import com.revrobotics.REVLibError
import com.revrobotics.SparkLimitSwitch
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.util.Units
import org.team9432.lib.constants.SwerveConstants.MK4I_L3_DRIVE_REDUCTION
import org.team9432.lib.constants.SwerveConstants.MK4I_STEER_REDUCTION
import org.team9432.lib.drivers.motors.KSparkFlex
import org.team9432.lib.drivers.motors.KSparkMAX
import org.team9432.robot.subsystems.drivetrain.ModuleIO.ModuleIOInputs


class ModuleIONeo(override val module: ModuleIO.Module) : ModuleIO {
    private val drive = KSparkFlex(module.driveID)
    private val steer = KSparkMAX(module.steerID)
    private val cancoder = CANcoder(module.encoderID)
    private val driveEncoder = drive.encoder
    private val steerEncoder = steer.encoder

    private val steerAbsolutePosition: StatusSignal<Double>

    init {
        drive.restoreFactoryDefaults()
        steer.restoreFactoryDefaults()

        for (i in 0..88) {
            drive.inverted = module.driveInverted
            steer.inverted = module.steerInverted

            if (drive.inverted == module.driveInverted && steer.inverted == module.steerInverted) break
        }

        for (i in 0..88) {
            val errors = mutableListOf<REVLibError>()
            errors += drive.setSmartCurrentLimit(40)
            errors += steer.setSmartCurrentLimit(30)

            errors += drive.enableVoltageCompensation(12.0)
            errors += steer.enableVoltageCompensation(12.0)

            errors += driveEncoder.setPosition(0.0)
            errors += driveEncoder.setMeasurementPeriod(10)
            errors += driveEncoder.setAverageDepth(2)

            errors += steerEncoder.setPosition(0.0)
            errors += steerEncoder.setMeasurementPeriod(10)
            errors += steerEncoder.setAverageDepth(2)

            errors += drive.getForwardLimitSwitch(SparkLimitSwitch.Type.kNormallyOpen).enableLimitSwitch(false)
            errors += drive.getReverseLimitSwitch(SparkLimitSwitch.Type.kNormallyOpen).enableLimitSwitch(false)

            errors += steer.getForwardLimitSwitch(SparkLimitSwitch.Type.kNormallyOpen).enableLimitSwitch(false)
            errors += steer.getReverseLimitSwitch(SparkLimitSwitch.Type.kNormallyOpen).enableLimitSwitch(false)

            if (errors.all { it == REVLibError.kOk }) break
        }

        drive.burnFlash()
        steer.burnFlash()

        val cancoderConfig = CANcoderConfiguration()
        cancoderConfig.MagnetSensor.AbsoluteSensorRange = AbsoluteSensorRangeValue.Unsigned_0To1
        cancoder.configurator.apply(cancoderConfig)

        steerAbsolutePosition = cancoder.absolutePosition
        BaseStatusSignal.setUpdateFrequencyForAll(50.0, steerAbsolutePosition)
        cancoder.optimizeBusUtilization()
    }

    override fun updateInputs(inputs: ModuleIOInputs) {
        BaseStatusSignal.refreshAll(steerAbsolutePosition)

        inputs.drivePositionRad = Units.rotationsToRadians(driveEncoder.position) / MK4I_L3_DRIVE_REDUCTION
        inputs.driveVelocityRadPerSec =
            Units.rotationsPerMinuteToRadiansPerSecond(driveEncoder.velocity) / MK4I_L3_DRIVE_REDUCTION
        inputs.driveAppliedVolts = drive.getAppliedOutput() * drive.getBusVoltage()
        inputs.driveCurrentAmps = drive.getOutputCurrent()

        inputs.steerAbsolutePosition =
            Rotation2d.fromRotations(steerAbsolutePosition.valueAsDouble).minus(module.encoderOffset)
        inputs.steerPosition = Rotation2d.fromRotations(steerEncoder.position / MK4I_STEER_REDUCTION)
        inputs.steerVelocityRadPerSec =
            (Units.rotationsPerMinuteToRadiansPerSecond(steerEncoder.velocity) / MK4I_STEER_REDUCTION)
        inputs.steerAppliedVolts = steer.getAppliedOutput() * steer.getBusVoltage()
        inputs.steerCurrentAmps = steer.getOutputCurrent()
    }

    override fun setDriveVoltage(volts: Double) = drive.setVoltage(volts)
    override fun setSteerVoltage(volts: Double) = steer.setVoltage(volts)

    override fun setBrakeMode(enabled: Boolean) {
        drive.idleMode = if (enabled) IdleMode.kBrake else IdleMode.kCoast
        steer.idleMode = if (enabled) IdleMode.kBrake else IdleMode.kCoast
    }
}
