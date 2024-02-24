package org.team9432.robot

import org.team9432.robot.subsystems.intake.Intake

enum class MechanismSide { SPEAKER, AMP }

object RobotState {
    fun noteInAmpSideIntake() = Intake.ampSideBeambreakActive
    fun noteInSpeakerSideIntake() = Intake.speakerSideBeambreakActive
}