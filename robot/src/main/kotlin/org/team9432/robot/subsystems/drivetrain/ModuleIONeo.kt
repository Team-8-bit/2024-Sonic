package org.team9432.robot.subsystems.drivetrain

import com.ctre.phoenix6.BaseStatusSignal
import com.ctre.phoenix6.StatusSignal
import com.ctre.phoenix6.configs.CANcoderConfiguration
import com.ctre.phoenix6.hardware.CANcoder
import com.ctre.phoenix6.signals.AbsoluteSensorRangeValue
import com.revrobotics.CANSparkBase.IdleMode
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.util.Units
import org.team9432.lib.constants.SwerveConstants.MK4I_L3_DRIVE_REDUCTION
import org.team9432.lib.constants.SwerveConstants.MK4I_STEER_REDUCTION
import org.team9432.lib.wrappers.SparkMax
import org.team9432.lib.wrappers.applyAndErrorCheck
import org.team9432.robot.subsystems.drivetrain.ModuleIO.ModuleIOInputs


class ModuleIONeo(override val module: ModuleIO.Module): ModuleIO {
    private val drive = SparkMax(module.driveID, "${module.name} Drive Motor")
    private val steer = SparkMax(module.steerID, "${module.name} Steer Motor")
    private val cancoder = CANcoder(module.encoderID)
    private val driveEncoder = drive.encoder
    private val steerEncoder = steer.encoder

    private val steerAbsolutePosition: StatusSignal<Double>

    init {
        val driveConfig = SparkMax.Config(
            inverted = module.driveInverted,
            idleMode = IdleMode.kBrake,
            smartCurrentLimit = 50,
        )

        val steerInverted = SparkMax.Config(
            inverted = module.steerInverted,
            idleMode = IdleMode.kBrake,
            smartCurrentLimit = 30,
        )

        drive.applyConfig(driveConfig)
        steer.applyConfig(steerInverted)

        applyAndErrorCheck("Drive Position") { driveEncoder.setPosition(0.0) }
        applyAndErrorCheck("Drive Measurement Period") { driveEncoder.setMeasurementPeriod(10) }
        applyAndErrorCheck("Drive Average Depth") { driveEncoder.setAverageDepth(2) }

        applyAndErrorCheck("Steer Position") { steerEncoder.setPosition(0.0) }
        applyAndErrorCheck("Steer Measurement Period") { steerEncoder.setMeasurementPeriod(10) }
        applyAndErrorCheck("Steer Average Depth") { steerEncoder.setAverageDepth(2) }

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
        inputs.driveVelocityRadPerSec = Units.rotationsPerMinuteToRadiansPerSecond(driveEncoder.velocity) / MK4I_L3_DRIVE_REDUCTION
        inputs.driveAppliedVolts = drive.getAppliedOutput() * drive.getBusVoltage()
        inputs.driveCurrentAmps = drive.getOutputCurrent()

        inputs.steerAbsolutePosition = Rotation2d.fromRotations(steerAbsolutePosition.valueAsDouble).minus(module.encoderOffset)
        inputs.steerPosition = Rotation2d.fromRotations(steerEncoder.position / MK4I_STEER_REDUCTION)
        inputs.steerVelocityRadPerSec = Units.rotationsPerMinuteToRadiansPerSecond(steerEncoder.velocity) / MK4I_STEER_REDUCTION
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
