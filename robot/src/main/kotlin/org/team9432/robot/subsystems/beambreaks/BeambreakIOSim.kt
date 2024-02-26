package org.team9432.robot.subsystems.beambreaks

class BeambreakIOSim: BeambreakIO {
    companion object {
        var intakeAmpSide = true
        var intakeSpeakerSide = true
        var hopperAmpSide = true
        var hopperSpeakerSide = true
        var center = true
    }

    override fun updateInputs(inputs: BeambreakIO.BeambreakIOInputs) {
        inputs.intakeAmpSideActive = intakeAmpSide
        inputs.intakeSpeakerSideActive = intakeSpeakerSide
        inputs.hopperAmpSideActive = hopperAmpSide
        inputs.hopperSpeakerSideActive = hopperSpeakerSide
        inputs.centerActive = center
    }
}