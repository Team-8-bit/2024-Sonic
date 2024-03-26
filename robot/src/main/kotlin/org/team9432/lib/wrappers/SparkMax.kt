package org.team9432.lib.wrappers

import com.revrobotics.CANSparkMax
import com.revrobotics.REVLibError
import com.revrobotics.SparkLimitSwitch
import edu.wpi.first.wpilibj.DriverStation

class SparkMax(canID: Int, val name: String, motorType: MotorType = MotorType.kBrushless): CANSparkMax(canID, motorType) {

    fun applyConfig(config: Config) {
        applySetting("Restore Defaults") { restoreFactoryDefaults() }

        applySetting("Inverted", { inverted = config.inverted }, { inverted == config.inverted })
        applySetting("Idle Mode") { setIdleMode(config.idleMode) }
        applySetting("Current Limit") { setSmartCurrentLimit(config.smartCurrentLimit) }
        applySetting("Voltage Compensation") { config.voltageCompensation?.let { enableVoltageCompensation(it) } ?: disableVoltageCompensation() }
        applySetting("Forwards Limit") { getForwardLimitSwitch(config.forwardLimitSwitchType).enableLimitSwitch(config.forwardLimitSwitchEnabled) }
        applySetting("Reverse Limit") { getReverseLimitSwitch(config.reverseLimitSwitchType).enableLimitSwitch(config.reverseLimitSwitchEnabled) }

        applySetting("Status Frame 0") { setPeriodicFramePeriod(PeriodicFrame.kStatus0, config.periodicFramePeriod0) }
        applySetting("Status Frame 1") { setPeriodicFramePeriod(PeriodicFrame.kStatus1, config.periodicFramePeriod1) }
        applySetting("Status Frame 2") { setPeriodicFramePeriod(PeriodicFrame.kStatus2, config.periodicFramePeriod2) }
        applySetting("Status Frame 3") { setPeriodicFramePeriod(PeriodicFrame.kStatus3, config.periodicFramePeriod3) }
        applySetting("Status Frame 4") { setPeriodicFramePeriod(PeriodicFrame.kStatus4, config.periodicFramePeriod4) }
        applySetting("Status Frame 5") { setPeriodicFramePeriod(PeriodicFrame.kStatus5, config.periodicFramePeriod5) }
        applySetting("Status Frame 6") { setPeriodicFramePeriod(PeriodicFrame.kStatus6, config.periodicFramePeriod6) }

        applySetting("Burn Flash") { burnFlash() }
    }

    fun applySetting(settingName: String, runnable: CANSparkMax.() -> Unit, hasSucceeded: () -> Boolean, attempts: Int = 88): Boolean {
        for (i in 0..attempts) {
            runnable()
            if (hasSucceeded()) return true
            else DriverStation.reportWarning("Retrying $settingName on $name (attempt $i/$attempts)", false)
        }
        DriverStation.reportError("Failed to set $settingName on $name after $attempts attempts", false)
        return false
    }

    fun applySetting(settingName: String, attempts: Int = 88, attempt: () -> REVLibError): Boolean {
        for (i in 1..attempts) {
            if (attempt() == REVLibError.kOk) return true
            else DriverStation.reportWarning("Retrying $settingName on $name (attempt $i/$attempts)", false)
        }
        DriverStation.reportError("Failed to set $settingName on $name after $attempts attempts", false)
        return false
    }

    data class Config(
        var inverted: Boolean = true,
        var idleMode: IdleMode = IdleMode.kBrake,
        var smartCurrentLimit: Int = 20,
        var voltageCompensation: Double? = 12.0,
        var forwardLimitSwitchType: SparkLimitSwitch.Type = SparkLimitSwitch.Type.kNormallyOpen,
        var forwardLimitSwitchEnabled: Boolean = false,
        var reverseLimitSwitchType: SparkLimitSwitch.Type = SparkLimitSwitch.Type.kNormallyOpen,
        var reverseLimitSwitchEnabled: Boolean = false,

        var periodicFramePeriod0: Int = 10,
        var periodicFramePeriod1: Int = 20,
        var periodicFramePeriod2: Int = 20,
        var periodicFramePeriod3: Int = 50,
        var periodicFramePeriod4: Int = 20,
        var periodicFramePeriod5: Int = 200,
        var periodicFramePeriod6: Int = 200,
    )
}