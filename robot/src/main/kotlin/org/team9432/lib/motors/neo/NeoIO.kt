package org.team9432.lib.motors.neo

import edu.wpi.first.math.geometry.Rotation2d
import org.team9432.lib.annotation.Logged

interface NeoIO {
    @Logged
    open class NEOIOInputs {
        var angle = Rotation2d()
        var appliedVolts = 0.0
        var currentAmps = 0.0
        var velocityRadPerSec = 0.0
    }

    fun updateInputs(inputs: NEOIOInputs) {}

    /* Run open loop at the specified voltage */
    fun setVoltage(volts: Double) {}

    /* Run closed loop angle control */
    fun setAngle(angle: Rotation2d) {}

    fun setPID(p: Double, i: Double, d: Double) {}

    fun resetEncoder(newAngle: Rotation2d = Rotation2d()) {}

    fun stop() {}
}