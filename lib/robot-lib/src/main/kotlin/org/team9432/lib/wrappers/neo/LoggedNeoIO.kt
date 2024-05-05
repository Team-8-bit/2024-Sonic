package org.team9432.lib.wrappers.neo

import edu.wpi.first.math.geometry.Rotation2d
import org.littletonrobotics.junction.LogTable
import org.littletonrobotics.junction.inputs.LoggableInputs
import org.team9432.lib.advantagekit.kGet
import org.team9432.lib.advantagekit.kPut

interface LoggedNeoIO {
    open class NEOIOInputs(private val additionalQualifier: String = ""): LoggableInputs {
        var angle = Rotation2d()
        var appliedVolts = 0.0
        var currentAmps = 0.0
        var velocityRadPerSec = 0.0

        override fun toLog(table: LogTable) {
            table.kPut("${additionalQualifier}Angle", angle)
            table.kPut("${additionalQualifier}AppliedVolts", appliedVolts)
            table.kPut("${additionalQualifier}CurrentAmps", currentAmps)
            table.kPut("${additionalQualifier}VelocityRadPerSec", velocityRadPerSec)
        }

        override fun fromLog(table: LogTable) {
            angle = table.kGet("${additionalQualifier}Angle", angle)
            appliedVolts = table.kGet("${additionalQualifier}AppliedVolts", appliedVolts)
            currentAmps = table.kGet("${additionalQualifier}CurrentAmps", currentAmps)
            velocityRadPerSec = table.kGet("${additionalQualifier}VelocityRadPerSec", velocityRadPerSec)
        }
    }

    fun updateInputs(inputs: NEOIOInputs) {}

    /* Run open loop at the specified voltage */
    fun setVoltage(volts: Double) {}

    fun setBrakeMode(enabled: Boolean) {}

    fun resetEncoder(newAngle: Rotation2d = Rotation2d()) {}

    fun stop() {}
}