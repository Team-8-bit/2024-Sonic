package org.team9432.robot.sensors.beambreaks

import edu.wpi.first.wpilibj.DigitalInput
import org.team9432.robot.Devices

class BeambreakIOReal: BeambreakIO {
    private val intakeAmpSide = DigitalInput(Devices.INTAKE_AMP_SIDE_BEAMBREAK_PORT)
    private val intakeSpeakerSide = DigitalInput(Devices.INTAKE_SPEAKER_SIDE_BEAMBREAK_PORT)
    private val hopperAmpSide = DigitalInput(Devices.HOPPER_AMP_SIDE_BEAMBREAK_PORT)
    private val hopperSpeakerSide = DigitalInput(Devices.HOPPER_SPEAKER_SIDE_BEAMBREAK_PORT)
    private val center = DigitalInput(Devices.CENTER_BEAMBREAK_PORT)

    override fun updateInputs(inputs: BeambreakIO.BeambreakIOInputs) {
        inputs.intakeAmpSideActive = intakeAmpSide.get()
        inputs.intakeSpeakerSideActive = intakeSpeakerSide.get()
        inputs.hopperAmpSideActive = hopperAmpSide.get()
        inputs.hopperSpeakerSideActive = hopperSpeakerSide.get()
        inputs.centerActive = center.get()
    }
}