package org.team9432.robot.sensors.beambreaks

import org.team9432.robot.MechanismSide

object BeambreakIOSim: BeambreakIO {
    fun setNoteInIntakeAmpSide(boolean: Boolean) { intakeAmpSide = !boolean }
    fun setNoteInIntakeSpeakerSide(boolean: Boolean) { intakeSpeakerSide = !boolean }
    fun setNoteInHopperAmpSide(boolean: Boolean) { hopperAmpSide = !boolean }
    fun setNoteInHopperSpeakerSide(boolean: Boolean) { hopperSpeakerSide = !boolean }
    fun setNoteInCenter(boolean: Boolean) { center = !boolean }

    fun setNoteInIntakeSide(side: MechanismSide, boolean: Boolean) {
        when (side) {
            MechanismSide.AMP -> setNoteInIntakeAmpSide(boolean)
            MechanismSide.SPEAKER -> setNoteInIntakeSpeakerSide(boolean)
        }
    }

    fun setNoteInHopperSide(side: MechanismSide, boolean: Boolean) {
        when (side) {
            MechanismSide.AMP -> setNoteInHopperAmpSide(boolean)
            MechanismSide.SPEAKER -> setNoteInHopperSpeakerSide(boolean)
        }
    }

    private var intakeAmpSide = true
    private var intakeSpeakerSide = true
    private var hopperAmpSide = true
    private var hopperSpeakerSide = true
    private var center = true

    override fun updateInputs(inputs: BeambreakIO.BeambreakIOInputs) {
        inputs.intakeAmpSideActive = intakeAmpSide
        inputs.intakeSpeakerSideActive = intakeSpeakerSide
        inputs.hopperAmpSideActive = hopperAmpSide
        inputs.hopperSpeakerSideActive = hopperSpeakerSide
        inputs.centerActive = center
    }
}