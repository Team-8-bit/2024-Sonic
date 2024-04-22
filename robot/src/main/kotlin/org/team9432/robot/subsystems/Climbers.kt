//package org.team9432.robot.subsystems
//
//import com.revrobotics.CANSparkBase
//import org.team9432.lib.commandbased.KSubsystem
//import org.team9432.lib.commandbased.commands.InstantCommand
//import org.team9432.lib.commandbased.commands.SimpleCommand
//import org.team9432.lib.logged.neo.LoggedNeo
//import org.team9432.lib.wrappers.Spark
//import org.team9432.robot.Devices
//
//object Climbers: KSubsystem() {
//    private val left = LoggedNeo(getConfig(62))
//    private val right = LoggedNeo(getConfig(61))
//
//    override fun periodic() {
//        left.updateAndRecordInputs()
//        right.updateAndRecordInputs()
//    }
//
//    fun setVoltageLeft(volts: Double) = left.setVoltage(volts)
//    fun setVoltageRight(volts: Double) = right.setVoltage(volts)
//    fun stopLeft() = left.stop()
//    fun stopRight() = right.stop()
//
//    object Commands {
//        fun setLeftVoltage(volts: Double) = InstantCommand(Climbers) { setVoltageLeft(volts) }
//        fun runLeftVoltage(volts: Double) = SimpleCommand(
//            requirements = setOf(Climbers),
//            initialize = { setVoltageLeft(volts) },
//            end = { stopLeft() }
//        )
//
//        fun setRightVoltage(volts: Double) = InstantCommand(Climbers) { setVoltageRight(volts) }
//        fun runRightVoltage(volts: Double) = SimpleCommand(
//            initialize = { setVoltageRight(volts) },
//            end = { stopRight() }
//        )
//    }
//
//    private fun getConfig(canID: Int) = LoggedNeo.Config(
//        canID = canID,
//        motorType = Spark.MotorType.NEO,
//        deviceName = "Amp",
//        logName = "Amp",
//        gearRatio = 1.0,
//        simJkgMetersSquared = 0.003,
//        sparkConfig = Spark.Config(
//            inverted = true,
//            idleMode = CANSparkBase.IdleMode.kBrake,
//            stallCurrentLimit = 60
//        )
//    )
//}