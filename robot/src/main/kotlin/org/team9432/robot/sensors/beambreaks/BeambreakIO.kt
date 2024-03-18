package org.team9432.robot.sensors.beambreaks

import org.team9432.lib.annotation.Logged

interface BeambreakIO {
    @Logged
    open class BeambreakIOInputs {
        var intakeAmpSideActive = true
        var intakeSpeakerSideActive = true
        var hopperAmpSideActive = true
        var hopperSpeakerSideActive = true
        var centerActive = true
    }

    fun updateInputs(inputs: BeambreakIOInputs) {}
}