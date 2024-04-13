package org.team9432.robot.subsystems

import com.revrobotics.CANSparkLowLevel
import com.revrobotics.CANSparkMax
import org.team9432.lib.commandbased.KSubsystem
import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.lib.commandbased.commands.SimpleCommand
import org.team9432.robot.Devices

object Bazooka: KSubsystem() {
    private val motor = CANSparkMax(Devices.BAZOOKA_ID, CANSparkLowLevel.MotorType.kBrushed)

    fun setVoltage(volts: Double) = motor.setVoltage(volts)
    fun stop() = motor.setVoltage(0.0)

    object Commands {
        fun setVoltage(volts: Double) = InstantCommand(Bazooka) { Bazooka.setVoltage(volts) }
        fun runVoltage(volts: Double) = SimpleCommand(
            requirements = setOf(Bazooka),
            initialize = { Bazooka.setVoltage(volts) },
            end = { Bazooka.stop() }
        )

        fun stop() = InstantCommand(Bazooka) { Bazooka.stop() }
    }
}