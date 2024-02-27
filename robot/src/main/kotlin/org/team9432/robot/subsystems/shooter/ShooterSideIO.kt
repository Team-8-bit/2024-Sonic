package org.team9432.robot.subsystems.shooter

import org.team9432.lib.annotation.Logged
import org.team9432.robot.Devices

interface ShooterSideIO {
    @Logged
    open class ShooterSideIOInputs {
        var velocityRPM = 0.0
        var appliedVolts = 0.0
        var currentAmps = 0.0
    }

    fun updateInputs(inputs: ShooterSideIOInputs) {}

    /* Run open loop at the specified voltage */
    fun setVoltage(volts: Double) {}

    /* Run closed loop speed control */
    fun setSpeed(rotationsPerMinute: Double, feedforwardVolts: Double) {}

    fun setPID(p: Double, i: Double, d: Double) {}

    fun stop() {}

    val shooterSide: ShooterSide

    enum class ShooterSide(
        val motorID: Int,
        val inverted: Boolean,
    ) {
        LEFT(
            motorID = Devices.LEFT_SHOOTER_ID,
            inverted = false
        ),
        RIGHT(
            motorID = Devices.RIGHT_SHOOTER_ID,
            inverted = true
        );
    }
}