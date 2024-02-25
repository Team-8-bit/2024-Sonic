package org.team9432.robot

import org.team9432.robot.subsystems.hopper.Hopper
import org.team9432.robot.subsystems.intake.Intake

enum class MechanismSide { SPEAKER, AMP }

object RobotState {
    fun noteInAmpSideIntake() = !Intake.ampSideBeambreakActive
    fun noteInSpeakerSideIntake() = !Intake.speakerSideBeambreakActive
    fun noteInCenterIntake() = !Intake.centerBeambreakActive
    fun noteInAmpSideHopper() = !Hopper.ampSideBeambreakActive
    fun noteInSpeakerSideHopper() = !Hopper.speakerSideBeambreakActive
}