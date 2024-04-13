package org.team9432.robot.subsystems

import com.revrobotics.CANSparkBase
import org.team9432.lib.commandbased.KSubsystem
import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.lib.commandbased.commands.SimpleCommand
import org.team9432.lib.logged.neo.LoggedNeo
import org.team9432.lib.wrappers.Spark
import org.team9432.robot.Devices

object Amp: KSubsystem() {
    private val motor = LoggedNeo(getConfig())

    override fun periodic() {
        motor.updateAndRecordInputs()
    }

    fun setVoltage(volts: Double) = motor.setVoltage(volts)
    fun stop() = motor.stop()

    object Commands {
        fun setVoltage(volts: Double) = InstantCommand(Amp) { Amp.setVoltage(volts) }
        fun runVoltage(volts: Double) = SimpleCommand(
            requirements = setOf(Amp),
            initialize = { Amp.setVoltage(volts) },
            end = { Amp.stop() }
        )
        fun stop() = InstantCommand(Amp) { Amp.stop() }
    }

    private fun getConfig() = LoggedNeo.Config(
        canID = Devices.AMP_ID,
        motorType = Spark.MotorType.NEO,
        deviceName = "Amp",
        logName = "Amp",
        gearRatio = 1.0,
        simJkgMetersSquared = 0.003,
        sparkConfig = Spark.Config(
            inverted = true,
            idleMode = CANSparkBase.IdleMode.kCoast,
            stallCurrentLimit = 60
        )
    )
}