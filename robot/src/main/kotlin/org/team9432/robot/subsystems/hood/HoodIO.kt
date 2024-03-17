package org.team9432.robot.subsystems.hood

import edu.wpi.first.math.geometry.Rotation2d
import org.team9432.lib.annotation.Logged

interface HoodIO {
    @Logged
    open class HoodIOInputs {
        var absoluteAngle = Rotation2d()
        var relativeAngle = Rotation2d()
        var appliedVolts = 0.0
        var currentAmps = 0.0
        var velocityDegPerSec = 0.0
    }

    fun updateInputs(inputs: HoodIOInputs) {}

    /* Run open loop at the specified voltage */
    fun setVoltage(volts: Double) {}

    /* Run closed loop angle control */
    fun setAngle(angle: Rotation2d) {}

    fun setPID(p: Double, i: Double, d: Double) {}

    fun setBrakeMode(enabled: Boolean) {}

    fun stop() {}
}