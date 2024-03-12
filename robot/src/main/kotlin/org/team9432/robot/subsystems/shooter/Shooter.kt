package org.team9432.robot.subsystems.shooter

import org.team9432.lib.commandbased.KSubsystem
import org.team9432.robot.subsystems.RobotPosition

object Shooter: KSubsystem() {
    private val leftSide = ShooterSide(ShooterSideIO.ShooterSide.LEFT)
    private val rightSide = ShooterSide(ShooterSideIO.ShooterSide.RIGHT)

    private var isRunningAtSpeeds: Pair<Double, Double>? = null

    override fun periodic() {
        leftSide.periodic()
        rightSide.periodic()

        isRunningAtSpeeds?.let {
            val (rpmFast, rpmSlow) = it

            val (leftSpeed, rightSpeed) = when (RobotPosition.getSpeakerSide()) {
                RobotPosition.SpeakerSide.LEFT -> rpmFast to rpmSlow
                RobotPosition.SpeakerSide.RIGHT -> rpmSlow to rpmFast
                RobotPosition.SpeakerSide.CENTER -> rpmSlow to rpmFast
            }
            leftSide.setSpeed(leftSpeed)
            rightSide.setSpeed(rightSpeed)
        }
    }

    fun startRunAtSpeeds(rpmFast: Double = 6000.0, rpmSlow: Double = 4000.0) {
        isRunningAtSpeeds = rpmFast to rpmSlow
    }

    fun setVoltage(leftVolts: Double, rightVolts: Double) {
        isRunningAtSpeeds = null
        leftSide.setVoltage(leftVolts)
        rightSide.setVoltage(rightVolts)
    }

    fun setSpeed(leftRPM: Double, rightRPM: Double) {
        isRunningAtSpeeds = null
        leftSide.setSpeed(leftRPM)
        rightSide.setSpeed(rightRPM)
    }

    fun stop() {
        isRunningAtSpeeds = null
        leftSide.stop()
        rightSide.stop()
    }
}