package org.team9432.robot.subsystems.intake

import org.team9432.lib.commandbased.KSubsystem
import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.robot.MechanismSide
import org.team9432.robot.RobotState

object Intake: KSubsystem() {
    private val ampSide = IntakeSide(IntakeSideIO.IntakeSide.AMP)
    private val speakerSide = IntakeSide(IntakeSideIO.IntakeSide.SPEAKER)

    fun setVoltage(ampSideVolts: Double, speakerSideVolts: Double) {
        ampSide.setVoltage(ampSideVolts)
        speakerSide.setVoltage(speakerSideVolts)
    }

    fun stop() {
        ampSide.stop()
        speakerSide.stop()
    }

    fun runCorrectIntake(volts: Double) {
        if (RobotState.shouldRunOneIntake()) {
            when (RobotState.getMovementDirection()) {
                MechanismSide.SPEAKER -> setVoltage(0.0, volts)
                MechanismSide.AMP -> setVoltage(volts, 0.0)
            }
        } else {
            setVoltage(volts, volts)
        }
    }

    fun runIntake(side: MechanismSide, volts: Double) {
        if (side == MechanismSide.SPEAKER) {
            speakerSide.setVoltage(volts)
        } else {
            ampSide.setVoltage(volts)
        }
    }

    fun setSpeed(ampSideRPM: Double, speakerSideRPM: Double) {
        ampSide.setSpeed(ampSideRPM)
        speakerSide.setSpeed(speakerSideRPM)
    }

    override fun periodic() {
        ampSide.periodic()
        speakerSide.periodic()
    }
}