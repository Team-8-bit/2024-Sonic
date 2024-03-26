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

    fun setVoltage(volts: Double) {}

    fun stop() {}
}