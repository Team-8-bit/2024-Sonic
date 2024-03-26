package org.team9432.robot.subsystems.intake

import com.revrobotics.CANSparkBase
import org.team9432.lib.commandbased.KSubsystem
import org.team9432.lib.motors.neo.NEO
import org.team9432.lib.wrappers.Spark
import org.team9432.robot.Devices
import org.team9432.robot.MechanismSide
import org.team9432.robot.RobotState
import kotlin.math.abs

object Intake: KSubsystem() {
    private val ampSide = NEO(getConfig(Devices.AMP_SIDE_INTAKE_ID, true, "Amp"))
    private val speakerSide = NEO(getConfig(Devices.SPEAKER_SIDE_INTAKE_ID, false, "Speaker"))

    private fun setVoltage(ampVolts: Double, speakerVolts: Double) {
        ampSide.setVoltage(ampVolts)
        speakerSide.setVoltage(speakerVolts)
    }

    fun intake(ampVolts: Double, speakerVolts: Double) {
        setVoltage(abs(ampVolts), abs(speakerVolts))
    }

    fun outtake(ampVolts: Double, speakerVolts: Double) {
        setVoltage(-abs(ampVolts), -abs(speakerVolts))
    }

    fun intakeSide(side: MechanismSide, volts: Double) {
        when (side) {
            MechanismSide.AMP -> intake(volts, 0.0)
            MechanismSide.SPEAKER -> intake(0.0, volts)
        }
    }

    fun outtakeSide(side: MechanismSide, volts: Double) {
        when (side) {
            MechanismSide.AMP -> outtake(volts, 0.0)
            MechanismSide.SPEAKER -> outtake(0.0, volts)
        }
    }

    fun stop() {
        ampSide.stop()
        speakerSide.stop()
    }

    fun runTeleIntake(volts: Double) {
        val absVolts = abs(volts)
        if (RobotState.shouldRunOneIntake()) {
            when (RobotState.getMovementDirection()) {
                MechanismSide.SPEAKER -> intake(0.0, absVolts)
                MechanismSide.AMP -> intake(absVolts, 0.0)
            }
        } else intake(absVolts, absVolts)
    }

    override fun periodic() {
        ampSide.periodic()
        speakerSide.periodic()
    }

    private fun getConfig(canID: Int, inverted: Boolean, side: String): NEO.Config {
        return NEO.Config(
            canID = canID,
            motorType = Spark.MotorType.NEO,
            name = "$side Side Intake",
            sparkConfig = Spark.Config(
                inverted = inverted,
                idleMode = CANSparkBase.IdleMode.kCoast,
                smartCurrentLimit = 80
            ),
            logName = "Intake/${side}Side",
            gearRatio = 2.0,
            simJkgMetersSquared = 0.003
        )
    }
}