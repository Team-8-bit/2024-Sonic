package org.team9432.robot

import org.team9432.robot.subsystems.beambreaks.Beambreaks
import org.team9432.robot.subsystems.hopper.Hopper
import org.team9432.robot.subsystems.intake.Intake

enum class MechanismSide { SPEAKER, AMP }

object RobotState {
    fun noteInAmpSideIntake() = !Beambreaks.getIntakeAmpSide()
    fun noteInSpeakerSideIntake() = !Beambreaks.getIntakeSpeakerSide()
    fun noteInCenter() = !Beambreaks.getHopperAmpSide()
    fun noteInAmpSideHopper() = !Beambreaks.getHopperSpeakerSide()
    fun noteInSpeakerSideHopper() = !Beambreaks.getCenter()
}