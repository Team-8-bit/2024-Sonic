package org.team9432.robot.subsystems.hopper

import com.revrobotics.CANSparkBase
import org.team9432.lib.commandbased.KSubsystem
import org.team9432.lib.motors.neo.NEO
import org.team9432.lib.wrappers.SparkMax
import org.team9432.robot.Devices
import org.team9432.robot.MechanismSide

object Hopper: KSubsystem() {
    private val motor = NEO(
        config = NEO.Config(
            canID = Devices.HOPPER_ID,
            name = "Hopper Motor",
            sparkConfig = SparkMax.Config(
                inverted = true,
                idleMode = CANSparkBase.IdleMode.kBrake,
                smartCurrentLimit = 60
            )
        ),
        logName = "Hopper",
        simGearRatio = 1.0,
        simJkgMetersSquared = 0.0015,
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