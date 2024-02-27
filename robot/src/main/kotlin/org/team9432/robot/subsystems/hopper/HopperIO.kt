package org.team9432.robot.subsystems.hopper

import org.team9432.lib.annotation.Logged

interface HopperIO {
    @Logged
    open class HopperIOInputs {
        var velocityRPM = 0.0
        var appliedVolts = 0.0
        var currentAmps = 0.0
    }

    fun updateInputs(inputs: HopperIOInputs) {}

    /* Run open loop at the specified voltage */
    fun setVoltage(volts: Double) {}

    /* Run closed loop speed control */
    fun setSpeed(rotationsPerMinute: Double, feedforwardVolts: Double) {}

    fun setPID(p: Double, i: Double, d: Double) {}

    fun stop() {}
}