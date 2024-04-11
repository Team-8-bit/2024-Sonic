package org.team9432.robot.oi.switches

import org.team9432.lib.commandbased.KPeriodic
import org.team9432.robot.MechanismSide

object DSSwitches: KPeriodic() {
    private val io = DSSwitchIOReal
    private val inputs = LoggedDSSwitchIOInputs()

    override fun periodic() {
        io.updateInputs(inputs)
    }

    val isTestSwitchActive get() = inputs.testSwitch
    val shouldUseAmpForSpeaker get() = inputs.useAmpForSpeaker

    val hoodDisabled get() = inputs.disableHood
    val drivetrainDisabled get() = inputs.disableDrivetrain

    val primaryScoringMechanism: MechanismSide
        get() {
            return if (!inputs.connected) MechanismSide.SPEAKER
            else if (inputs.primaryMechanismAmp) MechanismSide.AMP
            else MechanismSide.SPEAKER
        }
}