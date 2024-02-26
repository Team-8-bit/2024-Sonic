package org.team9432.robot

import org.team9432.robot.subsystems.beambreaks.Beambreaks

enum class MechanismSide { SPEAKER, AMP }

object RobotState {
    fun noteInAmpSideIntake() = !Beambreaks.getIntakeAmpSide()
    fun noteInSpeakerSideIntake() = !Beambreaks.getIntakeSpeakerSide()
    fun noteInCenter() = !Beambreaks.getHopperAmpSide()
    fun noteInAmpSideHopper() = !Beambreaks.getHopperSpeakerSide()
    fun noteInSpeakerSideHopper() = !Beambreaks.getCenter()

    fun noteInHopperSide(side: MechanismSide) = if (side == MechanismSide.SPEAKER) noteInSpeakerSideHopper() else noteInAmpSideHopper()
    fun noteInIntakeSide(side: MechanismSide) = if (side == MechanismSide.SPEAKER) noteInSpeakerSideIntake() else noteInAmpSideIntake()
}