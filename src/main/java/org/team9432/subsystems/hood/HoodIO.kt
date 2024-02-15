package org.team9432.subsystems.hood

import org.littletonrobotics.junction.LogTable
import org.littletonrobotics.junction.inputs.LoggableInputs

interface HoodIO {
    class HoodIOInputs: LoggableInputs {
        var absolutePosition = 0.0
        var atLimit = false

        override fun toLog(table: LogTable) {
            table.put("AbsolutePosition", absolutePosition)
            table.put("AtLimit", atLimit)
        }

        override fun fromLog(table: LogTable) {
            absolutePosition = table.get("AbsolutePosition", absolutePosition)
            atLimit = table.get("AtLimit", atLimit)
        }
    }

    fun updateInputs(inputs: HoodIOInputs)
    fun setAngle(angle: Double)
}