package org.team9432.robot.subsystems.shooter

import edu.wpi.first.math.geometry.Rotation2d
import org.team9432.lib.annotation.Logged

interface ShooterIO {
    @Logged
    open class ShooterIOInputs {
        var leftVelocityRPM = 0.0
        var rightVelocityRPM = 0.0
        var leftAppliedVolts = 0.0
        var rightAppliedVolts = 0.0
        var leftCurrentAmps = 0.0
        var rightCurrentAmps = 0.0
    }

    fun updateInputs(inputs: ShooterIOInputs) {}

    /* Run open loop at the specified voltage */
    fun setVoltage(leftVolts: Double, rightVolts: Double) {}

    /* Run closed loop speed control */
    fun setSpeed(leftRPM: Double, leftFFVolts: Double, rightRPM: Double, rightFFVolts: Double) {}

    fun setPID(p: Double, i: Double, d: Double) {}

    fun stop() {}
}