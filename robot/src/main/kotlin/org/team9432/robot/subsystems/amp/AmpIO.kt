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

    /* Run open loop at the specified voltage */
    fun setVoltage(volts: Double) {}

    /* Run closed loop speed control */
    fun setSpeed(rpm: Double, ffVolts: Double) {}

    fun setPID(p: Double, i: Double, d: Double) {}

    fun stop() {}
}