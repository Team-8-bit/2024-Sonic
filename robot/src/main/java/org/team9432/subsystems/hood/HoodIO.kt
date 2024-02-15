package org.team9432.subsystems.hood

import org.team9432.lib.annotation.Logged

interface HoodIO {
    @Logged
    open class HoodIOInputs {
        var absolutePosition = 0.0
        var atLimit = false
    }

    fun updateInputs(inputs: HoodIOInputs)
    fun setAngle(angle: Double)
}