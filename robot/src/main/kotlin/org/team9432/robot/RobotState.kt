package org.team9432.robot

import org.littletonrobotics.junction.Logger
import org.team9432.robot.subsystems.RobotPosition
import org.team9432.robot.subsystems.beambreaks.Beambreaks
import org.team9432.robot.subsystems.drivetrain.Drivetrain
import kotlin.math.abs

object RobotState {
    fun noteInAmpSideIntakeBeambreak() = !Beambreaks.getIntakeAmpSide()
    fun noteInSpeakerSideIntakeBeambreak() = !Beambreaks.getIntakeSpeakerSide()
    fun noteInCenterBeambreak() = !Beambreaks.getCenter()
    fun noteInAmpSideHopperBeambreak() = !Beambreaks.getHopperAmpSide()
    fun noteInSpeakerSideHopperBeambreak() = !Beambreaks.getHopperSpeakerSide()
    fun noteInHopperSide(side: MechanismSide) = if (side == MechanismSide.SPEAKER) noteInSpeakerSideHopperBeambreak() else noteInAmpSideHopperBeambreak()

    fun noteInIntakeSide(side: MechanismSide) = if (side == MechanismSide.SPEAKER) noteInSpeakerSideIntakeBeambreak() else noteInAmpSideIntakeBeambreak()
    fun noteInAnyHopper() = noteInAmpSideHopperBeambreak() || noteInSpeakerSideHopperBeambreak()

    fun noteInAnyIntake() = noteInAmpSideIntakeBeambreak() || noteInSpeakerSideIntakeBeambreak()

    // This prioritizes the amp side, but it should be really hard to actually get a note in both
    fun getOneIntakeBeambreak(): MechanismSide? {
        return if (noteInAmpSideIntakeBeambreak()) MechanismSide.AMP
        else if (noteInSpeakerSideIntakeBeambreak()) MechanismSide.SPEAKER
        else null
    }

    fun getMovementDirection(): MechanismSide {
        val speeds = Drivetrain.getSpeeds()
        if (speeds.vxMetersPerSecond > 0) return MechanismSide.SPEAKER else return MechanismSide.AMP
    }

    fun shouldRunOneIntake(): Boolean {
        val speeds = Drivetrain.getSpeeds()
        return maxOf(abs(speeds.vxMetersPerSecond), abs(speeds.vyMetersPerSecond)) > 1
    }

    var notePosition = NotePosition.NONE

    var isUsingApriltags = true
    var autoIsUsingApriltags = true
    var hasRemainingAutoNote = false

    fun log() {
        Logger.recordOutput("RobotState/NotePosition", notePosition.name)
        Logger.recordOutput("RobotState/MovementDirection", getMovementDirection())
        Logger.recordOutput("Drivetrain/SpeakerDistance", RobotPosition.distanceToSpeaker())
        Logger.recordOutput("RobotState/SpeakerPose", FieldConstants.speakerPose)
        Logger.recordOutput("RobotState/IsUsingAprilTags", isUsingApriltags)
    }

    enum class NotePosition {
        AMP_INTAKE, SPEAKER_INTAKE, AMP_HOPPER, SPEAKER_HOPPER, NONE;

        val isIntake get() = this == AMP_INTAKE || this == SPEAKER_INTAKE
        val isHopper get() = this == AMP_HOPPER || this == SPEAKER_HOPPER

        val side
            get() = when (this) {
                AMP_INTAKE, AMP_HOPPER -> MechanismSide.AMP
                SPEAKER_INTAKE, SPEAKER_HOPPER -> MechanismSide.SPEAKER
                NONE -> null
            }

    }

}

enum class MechanismSide {
    AMP, SPEAKER;

    fun getNotePositionIntake() = when (this) {
        AMP -> RobotState.NotePosition.AMP_INTAKE
        SPEAKER -> RobotState.NotePosition.SPEAKER_INTAKE
    }

    fun getNotePositionHopper() = when (this) {
        AMP -> RobotState.NotePosition.AMP_HOPPER
        SPEAKER -> RobotState.NotePosition.SPEAKER_HOPPER
    }
}
