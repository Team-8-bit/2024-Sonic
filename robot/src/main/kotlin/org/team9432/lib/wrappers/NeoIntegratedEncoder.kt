package org.team9432.lib.wrappers

import com.revrobotics.REVLibError
import edu.wpi.first.wpilibj.DriverStation

fun applyAndErrorCheck(settingName: String, runnable: () -> Unit, hasSucceeded: () -> Boolean, attempts: Int = 88): Boolean {
    for (i in 1..attempts) {
        runnable()
        if (hasSucceeded()) return true
        else DriverStation.reportWarning("Retrying $settingName (attempt $i/$attempts)", false)
    }
    DriverStation.reportError("Failed to set $settingName after $attempts attempts", false)
    return false
}

fun applyAndErrorCheck(settingName: String, attempts: Int = 88, attempt: () -> REVLibError): Boolean {
    for (i in 1..attempts) {
        if (attempt() == REVLibError.kOk) return true
        else DriverStation.reportWarning("Retrying $settingName (attempt $i/$attempts)", false)
    }
    DriverStation.reportError("Failed to set $settingName after $attempts attempts", false)
    return false
}