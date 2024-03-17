package org.team9432.robot.subsystems.amp

import org.team9432.lib.annotation.Logged

interface AmpIO {
    @Logged
    open class AmpIOInputs {
        var velocityRPM = 0.0
        var appliedVolts = 0.0
        var currentAmps = 0.0
    }

    fun updateInputs(inputs: AmpIOInputs)

    fun setVoltage(volts: Double) {}

    fun stop() {}
}