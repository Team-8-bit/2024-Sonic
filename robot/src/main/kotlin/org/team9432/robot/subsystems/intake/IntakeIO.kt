package org.team9432.robot.subsystems.intake

import org.team9432.lib.annotation.Logged

interface IntakeIO {
    @Logged
    open class IntakeIOInputs {
        var ampSideVelocityRPM = 0.0
        var ampSideAppliedVolts = 0.0
        var ampSideCurrentAmps = 0.0

        var speakerSideVelocityRPM = 0.0
        var speakerSideAppliedVolts = 0.0
        var speakerSideCurrentAmps = 0.0
    }

    fun updateInputs(inputs: IntakeIOInputs) {}

    /* Run open loop at the specified voltage */
    fun setVoltage(ampSideVolts: Double, speakerSideVolts: Double) {}

    /* Run closed loop speed control */
    fun setSpeed(ampSideRPM: Double, ampSideFFVolts: Double, speakerSideRPM: Double, speakerSideFFVolts: Double) {}

    fun setPID(p: Double, i: Double, d: Double) {}

    fun stop() {}
}