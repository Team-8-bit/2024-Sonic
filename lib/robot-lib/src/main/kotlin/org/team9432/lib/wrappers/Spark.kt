package org.team9432.lib.wrappers

import com.revrobotics.CANSparkBase
import com.revrobotics.CANSparkLowLevel
import com.revrobotics.REVLibError
import com.revrobotics.SparkLimitSwitch
import edu.wpi.first.wpilibj.DriverStation

/** A spark wrapper that provides safety when configuring parameters */
open class Spark(canID: Int, val name: String, motorType: MotorType): CANSparkBase(
    canID,
    CANSparkLowLevel.MotorType.kBrushless,
    when (motorType) {
        MotorType.NEO -> SparkModel.SparkMax
        MotorType.VORTEX -> SparkModel.SparkFlex
    }
) {
    /** Applies a given config to this device, retrying any failed attempts */
    fun applyConfig(config: Config) {
        applyAndErrorCheck("Restore Defaults") { restoreFactoryDefaults() }

        applyAndErrorCheck("Inverted", { inverted = config.inverted }, { inverted == config.inverted })
        applyAndErrorCheck("Idle Mode") { setIdleMode(config.idleMode) }
        applyAndErrorCheck("Current Limit") { setSmartCurrentLimit(config.stallCurrentLimit, config.freeCurrentLimit, config.currentLimitRpm) }
        applyAndErrorCheck("Voltage Compensation") { config.voltageCompensation?.let { enableVoltageCompensation(it) } ?: disableVoltageCompensation() }
        applyAndErrorCheck("Forwards Limit") { getForwardLimitSwitch(config.forwardLimitSwitchType).enableLimitSwitch(config.forwardLimitSwitchEnabled) }
        applyAndErrorCheck("Reverse Limit") { getReverseLimitSwitch(config.reverseLimitSwitchType).enableLimitSwitch(config.reverseLimitSwitchEnabled) }

        applyAndErrorCheck("Status Frame 0") { setPeriodicFramePeriod(PeriodicFrame.kStatus0, config.statusFrameConfig.periodicFramePeriod0) }
        applyAndErrorCheck("Status Frame 1") { setPeriodicFramePeriod(PeriodicFrame.kStatus1, config.statusFrameConfig.periodicFramePeriod1) }
        applyAndErrorCheck("Status Frame 2") { setPeriodicFramePeriod(PeriodicFrame.kStatus2, config.statusFrameConfig.periodicFramePeriod2) }
        applyAndErrorCheck("Status Frame 3") { setPeriodicFramePeriod(PeriodicFrame.kStatus3, config.statusFrameConfig.periodicFramePeriod3) }
        applyAndErrorCheck("Status Frame 4") { setPeriodicFramePeriod(PeriodicFrame.kStatus4, config.statusFrameConfig.periodicFramePeriod4) }
        applyAndErrorCheck("Status Frame 5") { setPeriodicFramePeriod(PeriodicFrame.kStatus5, config.statusFrameConfig.periodicFramePeriod5) }
        applyAndErrorCheck("Status Frame 6") { setPeriodicFramePeriod(PeriodicFrame.kStatus6, config.statusFrameConfig.periodicFramePeriod6) }

        applyAndErrorCheck("Burn Flash") { burnFlash() }
    }

    /** Safely set the motor in brake mode, retrying if it fails */
    fun setBrakeMode(enabled: Boolean) {
        val mode = if (enabled) IdleMode.kBrake else IdleMode.kCoast
        applyAndErrorCheck("Idle Mode") { setIdleMode(mode) }
    }

    /** Applies a given setting to the spark until hasSucceeded returns true or until the given number of attempts is reached */
    fun applyAndErrorCheck(settingName: String, runnable: () -> Unit, hasSucceeded: () -> Boolean, attempts: Int = 88): Boolean {
        for (i in 1..attempts) {
            runnable()
            if (hasSucceeded()) return true
            else DriverStation.reportWarning("Retrying $settingName on $name (attempt $i/$attempts)", false)
        }
        DriverStation.reportError("Failed to set $settingName on $name after $attempts attempts", false)
        return false
    }

    /** Applies a given setting to the spark until it returns REVLibError.kOk or until the given number of attempts is reached */
    fun applyAndErrorCheck(settingName: String, attempts: Int = 88, attempt: () -> REVLibError): Boolean {
        for (i in 1..attempts) {
            if (attempt() == REVLibError.kOk) return true
            else DriverStation.reportWarning("Retrying $settingName on $name (attempt $i/$attempts)", false)
        }
        DriverStation.reportError("Failed to set $settingName on $name after $attempts attempts", false)
        return false
    }

    /** A class describing the configuration options of a spark */
    data class Config(
        val inverted: Boolean = true,
        val idleMode: IdleMode = IdleMode.kBrake,
        val stallCurrentLimit: Int = 20,
        val freeCurrentLimit: Int = 0,
        val currentLimitRpm: Int = 20000,
        val voltageCompensation: Double? = 12.0,
        val forwardLimitSwitchType: SparkLimitSwitch.Type = SparkLimitSwitch.Type.kNormallyOpen,
        val forwardLimitSwitchEnabled: Boolean = false,
        val reverseLimitSwitchType: SparkLimitSwitch.Type = SparkLimitSwitch.Type.kNormallyOpen,
        val reverseLimitSwitchEnabled: Boolean = false,
        val statusFrameConfig: StatusFrameConfig = StatusFrameConfig(),
    ) {
        data class StatusFrameConfig(
            val periodicFramePeriod0: Int = 100,
            val periodicFramePeriod1: Int = 20,
            val periodicFramePeriod2: Int = 20,
            val periodicFramePeriod3: Int = 1000,
            val periodicFramePeriod4: Int = 1000,
            val periodicFramePeriod5: Int = 1000,
            val periodicFramePeriod6: Int = 1000,
        )
    }

    /** The default status frame timings provided by rev. Use .copy() to make modifications while retaining the rest of the defaults. */
    val revDefaultStatusFrameConfig = Config.StatusFrameConfig(
        periodicFramePeriod0 = 10,
        periodicFramePeriod1 = 20,
        periodicFramePeriod2 = 20,
        periodicFramePeriod3 = 50,
        periodicFramePeriod4 = 20,
        periodicFramePeriod5 = 200,
        periodicFramePeriod6 = 200
    )

    enum class MotorType {
        NEO, VORTEX
    }
}