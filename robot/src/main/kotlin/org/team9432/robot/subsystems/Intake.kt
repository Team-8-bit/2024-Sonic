package org.team9432.robot.subsystems

import com.revrobotics.CANSparkBase
import org.team9432.lib.commandbased.KSubsystem
import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.lib.commandbased.commands.SimpleCommand
import org.team9432.lib.motors.neo.Neo
import org.team9432.lib.wrappers.Spark
import org.team9432.robot.Devices
import org.team9432.robot.MechanismSide
import org.team9432.robot.RobotState
import kotlin.math.abs

object Intake: KSubsystem() {
    private val ampSide = Neo(getConfig(Devices.AMP_SIDE_INTAKE_ID, true, "Amp"))
    private val speakerSide = Neo(getConfig(Devices.SPEAKER_SIDE_INTAKE_ID, false, "Speaker"))

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

    object Commands {
        fun startIntake(ampVolts: Double, speakerVolts: Double) = InstantCommand(Intake) { intake(ampVolts, speakerVolts) }
        fun startOuttake(ampVolts: Double, speakerVolts: Double) = InstantCommand(Intake) { outtake(ampVolts, speakerVolts) }
        fun stop() = InstantCommand(Intake) { Intake.stop() }
        fun startTeleIntake(volts: Double) = InstantCommand(Intake) { Intake.runTeleIntake(volts) }
        fun startIntakeSide(side: MechanismSide, volts: Double) = InstantCommand(Intake) { intakeSide(side, volts) }
        fun startOuttakeSide(side: MechanismSide, volts: Double) = InstantCommand(Intake) { outtakeSide(side, volts) }

        fun runTeleIntake(volts: Double) = SimpleCommand(
            requirements = setOf(Intake),
            execute = { Intake.runTeleIntake(volts) },
            end = { Intake.stop() }
        )

        fun runIntakeSide(side: MechanismSide, volts: Double) = SimpleCommand(
            requirements = setOf(Intake),
            execute = { intakeSide(side, volts) },
            end = { Intake.stop() }
        )

        fun runOuttakeSide(side: MechanismSide, volts: Double) = SimpleCommand(
            requirements = setOf(Intake),
            execute = { outtakeSide(side, volts) },
            end = { Intake.stop() }
        )
    }

    private fun getConfig(canID: Int, inverted: Boolean, side: String): Neo.Config {
        return Neo.Config(
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