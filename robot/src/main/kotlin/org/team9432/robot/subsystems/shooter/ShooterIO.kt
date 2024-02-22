package org.team9432.robot.subsystems.shooter

import edu.wpi.first.math.geometry.Rotation2d
import org.team9432.lib.annotation.Logged

interface ShooterIO {
    @Logged
    open class ShooterIOInputs {
        var velocityRPM = 0.0
        var appliedVolts = 0.0
        var currentAmps = doubleArrayOf()
    }

    fun updateInputs(inputs: ShooterIOInputs) {}

    /* Run open loop at the specified voltage */
    fun setVoltage(volts: Double) {}

    /* Run closed loop speed control */
    fun setSpeed(rotationPerMinute: Double, feedforwardVolts: Double) {}

    fun setPID(p: Double, i: Double, d: Double) {}

    fun stop() {}
}