package org.team9432.robot.subsystems.intake

import org.team9432.lib.commandbased.KSubsystem
import org.team9432.robot.MechanismSide
import org.team9432.robot.RobotState
import kotlin.math.abs

object Intake: KSubsystem() {
    private val ampSide = IntakeSide(IntakeSideIO.IntakeSide.AMP)
    private val speakerSide = IntakeSide(IntakeSideIO.IntakeSide.SPEAKER)

    private var teleIntakeVolts: Double? = null // Null means that tele mode is not enabled

    private fun setVoltage(ampVolts: Double, speakerVolts: Double) {
        ampSide.setVoltage(ampVolts)
        speakerSide.setVoltage(speakerVolts)
    }

    fun intake(ampVolts: Double, speakerVolts: Double) {
        teleIntakeVolts = null
        setVoltage(abs(ampVolts), abs(speakerVolts))
    }

    fun outtake(ampVolts: Double, speakerVolts: Double) {
        teleIntakeVolts = null
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
        teleIntakeVolts = null
        ampSide.stop()
        speakerSide.stop()
    }

    fun runTeleIntake(volts: Double) {
        teleIntakeVolts = abs(volts)
    }

    override fun periodic() {
        ampSide.periodic()
        speakerSide.periodic()

        teleIntakeVolts?.let { volts ->
            if (RobotState.shouldRunOneIntake()) {
                when (RobotState.getMovementDirection()) {
                    MechanismSide.SPEAKER -> intake(0.0, volts)
                    MechanismSide.AMP -> intake(volts, 0.0)
                }
            } else intake(volts, volts)
        }
    }
}