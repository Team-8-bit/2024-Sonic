package org.team9432.robot.oi.switches

import org.team9432.lib.annotation.Logged

interface DSSwitchIO {
    @Logged
    open class DSSwitchIOInputs {
        var connected = false
        var disableHood = false
        var disableDrivetrain = false
        var useAmpForSpeaker = false
        var testSwitch = false
        var teleAutoAimDisabled = false
        var primaryMechanismAmp = false
    }

    fun updateInputs(inputs: DSSwitchIOInputs) {}
}