package org.team9432.robot.subsystems

import com.revrobotics.CANSparkBase
import org.team9432.lib.commandbased.KSubsystem
import org.team9432.lib.commandbased.commands.SimpleCommand
import org.team9432.lib.wrappers.Spark
import org.team9432.lib.wrappers.neo.LoggedNeo
import org.team9432.robot.Devices.LEFT_CLIMBER
import org.team9432.robot.Devices.RIGHT_CLIMBER

object Climbers: KSubsystem() {
    private val left = LoggedNeo(getConfig(LEFT_CLIMBER))
    private val right = LoggedNeo(getConfig(RIGHT_CLIMBER))

    override fun periodic() {
        left.updateAndRecordInputs()
        right.updateAndRecordInputs()
    }

    fun setVoltageLeft(volts: Double) = left.setVoltage(volts)
    fun setVoltageRight(volts: Double) = right.setVoltage(volts)
    fun stopLeft() = left.stop()
    fun stopRight() = right.stop()

    object Commands {
        fun runLeftVoltage(volts: Double) = SimpleCommand(
            initialize = { setVoltageLeft(volts) },
            end = { stopLeft() }
        )

        fun runRightVoltage(volts: Double) = SimpleCommand(
            initialize = { setVoltageRight(volts) },
            end = { stopRight() }
        )
    }

    private fun getConfig(canID: Int) = LoggedNeo.Config(
        canID = canID,
        motorType = Spark.MotorType.NEO,
        deviceName = "Climber",
        logName = "Climber",
        gearRatio = 1.0,
        simJkgMetersSquared = 0.003,
        sparkConfig = Spark.Config(
            inverted = true,
            idleMode = CANSparkBase.IdleMode.kBrake,
            stallCurrentLimit = 60
        )
    )
}