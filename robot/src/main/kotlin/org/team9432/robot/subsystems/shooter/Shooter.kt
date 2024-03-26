package org.team9432.robot.subsystems.shooter

import com.revrobotics.CANSparkBase
import org.team9432.lib.commandbased.KSubsystem
import org.team9432.lib.motors.neo.NEO
import org.team9432.lib.wrappers.Spark
import org.team9432.robot.Devices
import org.team9432.robot.subsystems.RobotPosition

object Shooter: KSubsystem() {
    private val leftSide = NEO(getConfig(Devices.LEFT_SHOOTER_ID, false, "Left"))
    private val rightSide = NEO(getConfig(Devices.RIGHT_SHOOTER_ID, true, "Right"))

    private var isRunningAtSpeeds: Pair<Int, Int>? = null

    override fun periodic() {
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

    fun startRunAtSpeeds(rpmFast: Int = 6000, rpmSlow: Int = 4000) {
        isRunningAtSpeeds = rpmFast to rpmSlow
    }

    fun setVoltage(leftVolts: Double, rightVolts: Double) {
        isRunningAtSpeeds = null
        leftSide.setVoltage(leftVolts)
        rightSide.setVoltage(rightVolts)
    }

    fun setSpeed(leftRPM: Int, rightRPM: Int) {
        isRunningAtSpeeds = null
        leftSide.setSpeed(leftRPM)
        rightSide.setSpeed(rightRPM)
    }

    fun stop() {
        isRunningAtSpeeds = null
        leftSide.stop()
        rightSide.stop()
    }

    private fun getConfig(canID: Int, inverted: Boolean, side: String): NEO.Config {
        return NEO.Config(
            canID = canID,
            motorType = Spark.MotorType.VORTEX,
            name = "$side Shooter",
            sparkConfig = Spark.Config(
                inverted = inverted,
                idleMode = CANSparkBase.IdleMode.kCoast,
                smartCurrentLimit = 80
            ),
            logName = "Shooter/${side}Side",
            gearRatio = 0.5,
            simJkgMetersSquared = 0.003
        )
    }
}