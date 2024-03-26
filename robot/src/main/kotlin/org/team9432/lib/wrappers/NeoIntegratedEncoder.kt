package org.team9432.lib.wrappers

import com.revrobotics.REVLibError
import edu.wpi.first.wpilibj.DriverStation

/** Applies a given setting to the encoder until hasSucceeded returns true or until the given number of attempts is reached */
fun applyAndErrorCheck(settingName: String, runnable: () -> Unit, hasSucceeded: () -> Boolean, attempts: Int = 88): Boolean {
    for (i in 1..attempts) {
        runnable()
        if (hasSucceeded()) return true
        else DriverStation.reportWarning("Retrying $settingName (attempt $i/$attempts)", false)
    }
    DriverStation.reportError("Failed to set $settingName after $attempts attempts", false)
    return false
}

/** Applies a given setting to the encoder until it returns REVLibError.kOk or until the given number of attempts is reached */
fun applyAndErrorCheck(settingName: String, attempts: Int = 88, attempt: () -> REVLibError): Boolean {
    for (i in 1..attempts) {
        if (attempt() == REVLibError.kOk) return true
        else DriverStation.reportWarning("Retrying $settingName (attempt $i/$attempts)", false)
    }
    DriverStation.reportError("Failed to set $settingName after $attempts attempts", false)
    return false
}