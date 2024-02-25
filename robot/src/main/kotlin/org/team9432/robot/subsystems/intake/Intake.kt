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

    fun runVolts(ampSideVolts: Double, speakerSideVolts: Double) {
        ampSide.runVolts(ampSideVolts)
        speakerSide.runVolts(speakerSideVolts)
    }

    fun stopCommand() = InstantCommand(requirements = mutableSetOf(Intake)) { stop() }

    fun setSpeed(ampSideRPM: Double, speakerSideRPM: Double) {
        ampSide.runVolts(ampSideRPM)
        speakerSide.runVolts(speakerSideRPM)
    }

    fun stop() {
        ampSide.stop()
        speakerSide.stop()
    }
}