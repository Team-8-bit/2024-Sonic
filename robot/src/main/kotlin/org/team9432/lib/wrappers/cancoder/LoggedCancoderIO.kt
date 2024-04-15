package org.team9432.lib.wrappers.cancoder

import edu.wpi.first.math.geometry.Rotation2d
import org.littletonrobotics.junction.LogTable
import org.littletonrobotics.junction.inputs.LoggableInputs
import org.team9432.lib.advantagekit.kGet
import org.team9432.lib.advantagekit.kPut

interface LoggedCancoderIO {
    open class CancoderIOInputs(private val additionalQualifier: String = ""): LoggableInputs {
        var position = Rotation2d()

        override fun toLog(table: LogTable) {
            table.kPut("${additionalQualifier}Position", position)
        }

        override fun fromLog(table: LogTable) {
            position = table.kGet("${additionalQualifier}Position", position)
        }
    }

    fun updateInputs(inputs: CancoderIOInputs) {}
}