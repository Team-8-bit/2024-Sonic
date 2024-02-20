package org.team9432.robot.subsystems

import org.team9432.lib.annotation.Logged

interface AmpIO {
    @Logged
    open class AmpIOInputs {
    }

    fun updateInputs(inputs: AmpIOInputs)
    fun setSpeed(speed: Double)
}