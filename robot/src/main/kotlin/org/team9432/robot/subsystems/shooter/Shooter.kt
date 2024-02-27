package org.team9432.robot.subsystems.shooter

import org.team9432.lib.commandbased.KSubsystem

object Shooter: KSubsystem() {
    private val leftSide = ShooterSide(ShooterSideIO.ShooterSide.LEFT)
    private val rightSide = ShooterSide(ShooterSideIO.ShooterSide.RIGHT)

    override fun periodic() {
        leftSide.periodic()
        rightSide.periodic()
    }

    fun setVoltage(leftVolts: Double, rightVolts: Double) {
        leftSide.setVoltage(leftVolts)
        rightSide.setVoltage(rightVolts)
    }

    fun setSpeed(leftRPM: Double, rightRPM: Double) {
        leftSide.setSpeed(leftRPM)
        rightSide.setSpeed(rightRPM)
    }

    fun stop() {
        leftSide.stop()
        rightSide.stop()
    }
}