package org.team9432.robot.subsystems.hopper

import org.team9432.lib.commandbased.KSubsystem
import org.team9432.lib.wrappers.neo.NEO
import org.team9432.robot.Devices
import org.team9432.robot.MechanismSide

object Hopper: KSubsystem() {
    private val motor = NEO(
        canID = Devices.HOPPER_ID,
        motorName = "Hopper Motor", "Hopper", 1.0, 0.0015
    )

    fun setVoltage(volts: Double) {
        motor.setVoltage(volts)
    }

    fun loadTo(side: MechanismSide, volts: Double) =
        if (side == MechanismSide.SPEAKER) setVoltage(-volts) else setVoltage(volts)

    fun unloadFrom(side: MechanismSide, volts: Double) =
        if (side == MechanismSide.SPEAKER) setVoltage(volts) else setVoltage(-volts)

    fun stop() = motor.stop()
}