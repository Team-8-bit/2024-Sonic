package org.team9432.robot.subsystems.amp

import com.revrobotics.CANSparkBase
import org.team9432.lib.commandbased.KSubsystem
import org.team9432.lib.motors.neo.Neo
import org.team9432.lib.wrappers.Spark
import org.team9432.robot.Devices

object Amp: KSubsystem() {
    private val motor = Neo(getConfig())

    fun setVoltage(volts: Double) = motor.setVoltage(volts)
    fun stop() = motor.stop()

    private fun getConfig() = Neo.Config(
        canID = Devices.AMP_ID,
        motorType = Spark.MotorType.NEO,
        name = "Amp Motor",
        logName = "Amp",
        gearRatio = 1.0,
        simJkgMetersSquared = 0.003,
        sparkConfig = Spark.Config(
            inverted = true,
            idleMode = CANSparkBase.IdleMode.kCoast,
            smartCurrentLimit = 60
        )
    )
}