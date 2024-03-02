package org.team9432.robot.subsystems.climber

import edu.wpi.first.math.geometry.Rotation2d
import org.team9432.lib.annotation.Logged
import org.team9432.robot.Devices

interface ClimberSideIO {
    @Logged
    open class ClimberSideIOInputs {
        var position = Rotation2d()
        var velocityRadPerSec = 0.0
        var atLimit = false
        var appliedVolts = 0.0
        var currentAmps = 0.0
    }

    fun updateInputs(inputs: ClimberSideIOInputs) {}

    fun setVoltage(volts: Double) {}
    fun setAngle(angle: Rotation2d, feedforwardVolts: Double) {}

    fun setPID(p: Double, i: Double, d: Double) {}

    fun setBrakeMode(enabled: Boolean) {}

    fun stop() {}

    val climberSide: ClimberSide

    enum class ClimberSide(
        val motorID: Int,
        val limitPort: Int,
        val inverted: Boolean,
    ) {
        RIGHT(
            motorID = Devices.RIGHT_CLIMBER_ID,
            limitPort = Devices.RIGHT_CLIMBER_LIMIT_PORT,
            inverted = true
        ),
        LEFT(
            motorID = Devices.LEFT_CLIMBER_ID,
            limitPort = Devices.LEFT_CLIMBER_LIMIT_PORT,
            inverted = false
        );
    }
}