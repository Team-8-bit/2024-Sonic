package org.team9432.robot.subsystems.beambreaks

import org.team9432.lib.annotation.Logged

interface BeambreakIO {
    @Logged
    open class BeambreakIOInputs {
        var intakeAmpSideActive = false
        var intakeSpeakerSideActive = false
        var hopperAmpSideActive = false
        var hopperSpeakerSideActive = false
        var centerActive = false
    }

    fun updateInputs(inputs: BeambreakIOInputs) {}
}