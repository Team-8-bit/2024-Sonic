package org.team9432.robot.subsystems.intake

import org.team9432.lib.annotation.Logged
import org.team9432.robot.Devices

interface IntakeSideIO {
    @Logged
    open class IntakeSideIOInputs {
        var velocityRPM = 0.0
        var appliedVolts = 0.0
        var currentAmps = 0.0
    }

    fun updateInputs(inputs: IntakeSideIOInputs) {}

    /* Run open loop at the specified voltage */
    fun setVoltage(volts: Double) {}

    /* Run closed loop speed control */
    fun setSpeed(rotationsPerMinute: Double, feedForwardVolts: Double) {}

    fun setPID(p: Double, i: Double, d: Double) {}

    fun stop() {}

    val intakeSide: IntakeSide

    enum class IntakeSide(
        val motorID: Int,
        val inverted: Boolean,
    ) {
        AMP(
            motorID = Devices.AMP_SIDE_INTAKE_ID,
            inverted = true
        ),
        SPEAKER(
            motorID = Devices.SPEAKER_SIDE_INTAKE_ID,
            inverted = false
        );
    }
}