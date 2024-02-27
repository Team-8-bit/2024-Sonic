package org.team9432.robot.subsystems.beambreaks

import org.team9432.robot.MechanismSide

class BeambreakIOSim: BeambreakIO {
    companion object {
        fun setNoteInIntakeAmpSide(boolean: Boolean) { intakeAmpSide = !boolean }
        fun setNoteInIntakeSpeakerSide(boolean: Boolean) { intakeSpeakerSide = !boolean }
        fun setNoteInHopperAmpSide(boolean: Boolean) { hopperAmpSide = !boolean }
        fun setNoteInHopperSpeakerSide(boolean: Boolean) { hopperSpeakerSide = !boolean }
        fun setNoteInCenter(boolean: Boolean) { center = !boolean }

        fun setNoteInIntake(side: MechanismSide, boolean: Boolean) {
            if (side == MechanismSide.SPEAKER) setNoteInIntakeSpeakerSide(boolean)
            else setNoteInIntakeAmpSide(boolean)
        }

        fun setNoteInHopper(side: MechanismSide, boolean: Boolean) {
            if (side == MechanismSide.SPEAKER) setNoteInHopperSpeakerSide(boolean)
            else setNoteInHopperAmpSide(boolean)
        }

        private var intakeAmpSide = true
        private var intakeSpeakerSide = true
        private var hopperAmpSide = true
        private var hopperSpeakerSide = true
        private var center = true
    }

    override fun updateInputs(inputs: BeambreakIO.BeambreakIOInputs) {
        inputs.intakeAmpSideActive = intakeAmpSide
        inputs.intakeSpeakerSideActive = intakeSpeakerSide
        inputs.hopperAmpSideActive = hopperAmpSide
        inputs.hopperSpeakerSideActive = hopperSpeakerSide
        inputs.centerActive = center
    }
}