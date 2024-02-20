package org.team9432.robot.subsystems.hopper

import org.team9432.lib.annotation.Logged

interface HopperIO {
    @Logged
    open class HopperIOInputs {
        var atAmpBeamBrake = false
        var atShooterBeamBrake = false
    }

    fun updateInputs(inputs: HopperIOInputs)
    fun setSpeed(speed: Double)
}