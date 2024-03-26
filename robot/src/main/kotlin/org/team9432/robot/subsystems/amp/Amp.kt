package org.team9432.robot.subsystems.amp

import com.revrobotics.CANSparkBase
import org.team9432.lib.commandbased.KSubsystem
import org.team9432.lib.motors.neo.NEO
import org.team9432.lib.wrappers.SparkMax
import org.team9432.robot.Devices

object Amp: KSubsystem() {
    private val motor = NEO(getConfig())

    fun setVoltage(volts: Double) = motor.setVoltage(volts)
    fun stop() = motor.stop()

    private fun getConfig() = NEO.Config(
        canID = Devices.AMP_ID,
        name = "Amp Motor",
        logName = "Amp",
        gearRatio = 1.0,
        simJkgMetersSquared = 0.003,
        sparkConfig = SparkMax.Config(
            inverted = true,
            idleMode = CANSparkBase.IdleMode.kCoast,
            smartCurrentLimit = 60
        )
    )
}