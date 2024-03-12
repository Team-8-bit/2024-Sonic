package org.team9432.robot.subsystems.shooter

import org.team9432.lib.commandbased.commands.InstantCommand

/* Interface for interacting with the subsystem through command based systems */
object CommandShooter {
    fun setVoltage(leftVolts: Double, rightVolts: Double) = InstantCommand(Shooter) { Shooter.setVoltage(leftVolts, rightVolts) }
    fun setSpeed(leftRPM: Double, rightRPM: Double) = InstantCommand(Shooter) { Shooter.setSpeed(leftRPM, rightRPM) }
    fun stop() = InstantCommand(Shooter) { Shooter.stop() }

    fun startRunAtSpeeds(rpmFast: Double = 6000.0, rpmSlow: Double = 4000.0) = InstantCommand(Shooter) { Shooter.startRunAtSpeeds(rpmFast, rpmSlow) }
}