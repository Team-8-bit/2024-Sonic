package org.team9432.robot.subsystems.intake

import org.team9432.lib.commandbased.KSubsystem
import org.team9432.lib.commandbased.commands.InstantCommand

object Intake: KSubsystem() {
    private val ampSide = IntakeSide(IntakeSideIO.IntakeSide.AMP)
    private val speakerSide = IntakeSide(IntakeSideIO.IntakeSide.SPEAKER)

    override fun constantPeriodic() {
        ampSide.periodic()
        speakerSide.periodic()
    }

    fun setVoltage(ampSideVolts: Double, speakerSideVolts: Double) = InstantCommand(Intake) {
        ampSide.setVoltage(ampSideVolts)
        speakerSide.setVoltage(speakerSideVolts)
    }

    fun stopCommand() = InstantCommand(Intake) { stop() }

    fun setSpeed(ampSideRPM: Double, speakerSideRPM: Double) = InstantCommand(Intake) {
        ampSide.setSpeed(ampSideRPM)
        speakerSide.setSpeed(speakerSideRPM)
    }

    fun stop() {
        ampSide.stop()
        speakerSide.stop()
    }
}