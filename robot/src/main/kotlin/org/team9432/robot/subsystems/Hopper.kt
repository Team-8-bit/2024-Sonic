package org.team9432.robot.subsystems

import com.revrobotics.CANSparkBase
import org.team9432.lib.commandbased.KSubsystem
import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.lib.commandbased.commands.SimpleCommand
import org.team9432.lib.motors.neo.Neo
import org.team9432.lib.wrappers.Spark
import org.team9432.robot.Devices
import org.team9432.robot.MechanismSide

object Hopper: KSubsystem() {
    private val motor = Neo(getConfig())

    fun setVoltage(volts: Double) {
        motor.setVoltage(volts)
    }

    fun loadTo(side: MechanismSide, volts: Double) =
        if (side == MechanismSide.SPEAKER) setVoltage(-volts) else setVoltage(volts)

    fun unloadFrom(side: MechanismSide, volts: Double) =
        if (side == MechanismSide.SPEAKER) setVoltage(volts) else setVoltage(-volts)

    fun stop() = motor.stop()

    object Commands {
        fun setVoltage(volts: Double) = InstantCommand(Hopper) { Hopper.setVoltage(volts) }
        fun stop() = InstantCommand(Hopper) { Hopper.stop() }

        fun startLoadTo(side: MechanismSide, volts: Double) = InstantCommand(Hopper) { loadTo(side, volts) }
        fun startUnloadFrom(side: MechanismSide, volts: Double) = InstantCommand(Hopper) { unloadFrom(side, volts) }

        fun runLoadTo(side: MechanismSide, volts: Double) = SimpleCommand(
            requirements = setOf(Hopper),
            execute = { loadTo(side, volts) },
            end = { Hopper.stop() }
        )

        fun runUnloadFrom(side: MechanismSide, volts: Double) = SimpleCommand(
            requirements = setOf(Hopper),
            execute = { unloadFrom(side, volts) },
            end = { Hopper.stop() }
        )
    }

    private fun getConfig() = Neo.Config(
        canID = Devices.HOPPER_ID,
        motorType = Spark.MotorType.NEO,
        name = "Hopper Motor",
        logName = "Hopper",
        gearRatio = 1.0,
        simJkgMetersSquared = 0.0015,
        sparkConfig = Spark.Config(
            inverted = true,
            idleMode = CANSparkBase.IdleMode.kBrake,
            smartCurrentLimit = 60
        )
    )
}
