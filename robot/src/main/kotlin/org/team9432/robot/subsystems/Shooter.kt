package org.team9432.robot.subsystems

import com.revrobotics.CANSparkBase
import org.team9432.lib.commandbased.KSubsystem
import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.lib.motors.neo.Neo
import org.team9432.lib.wrappers.Spark
import org.team9432.robot.Devices
import org.team9432.robot.RobotPosition

object Shooter: KSubsystem() {
    private val leftSide = Neo(getConfig(Devices.LEFT_SHOOTER_ID, false, "Left"))
    private val rightSide = Neo(getConfig(Devices.RIGHT_SHOOTER_ID, true, "Right"))

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

    object Commands {
        fun setVoltage(leftVolts: Double, rightVolts: Double) = InstantCommand(Shooter) { Shooter.setVoltage(leftVolts, rightVolts) }
        fun setSpeed(leftRPM: Int, rightRPM: Int) = InstantCommand(Shooter) { Shooter.setSpeed(leftRPM, rightRPM) }
        fun stop() = InstantCommand(Shooter) { Shooter.stop() }

        fun startRunAtSpeeds(rpmFast: Int = 6000, rpmSlow: Int = 4000) = InstantCommand(Shooter) { Shooter.startRunAtSpeeds(rpmFast, rpmSlow) }
    }

    private fun getConfig(canID: Int, inverted: Boolean, side: String): Neo.Config {
        return Neo.Config(
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