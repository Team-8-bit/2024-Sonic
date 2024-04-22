package org.team9432.robot

import edu.wpi.first.math.geometry.Pose2d
import org.littletonrobotics.junction.Logger
import org.team9432.lib.commandbased.KPeriodic
import org.team9432.robot.auto.AutoConstants
import org.team9432.robot.sensors.beambreaks.Beambreaks
import org.team9432.robot.subsystems.drivetrain.Drivetrain
import kotlin.math.abs

object RobotState: KPeriodic() {
    override fun periodic() {
        Logger.recordOutput("RobotState/NotePosition", notePosition.name)
        Logger.recordOutput("RobotState/MovementDirection", getMovementDirection())
        Logger.recordOutput("RobotState/SpeakerDistance", RobotPosition.distanceToSpeaker())
        Logger.recordOutput("RobotState/SpeakerPose", FieldConstants.speakerAimPose)
        Logger.recordOutput("RobotState/TrapAimPoints", *FieldConstants.trapAimPoses.toTypedArray())
        Logger.recordOutput("RobotState/TrapAimPose", FieldConstants.getTrapAimPosition())

        Logger.recordOutput("AutoPoses/CenterCenter/Centerstage", AutoConstants.centerStage)
        Logger.recordOutput("AutoPoses/CenterCenter/ShotPose", AutoConstants.centerCenterShot)
        Logger.recordOutput("AutoPoses/CenterCenter/Intake", AutoConstants.centerNoteThreeIntakePose)
        Logger.recordOutput("AutoPoses/CenterCenter/Drive", AutoConstants.centerCenterDriveOne)

        Logger.recordOutput("FeedThing", FieldConstants.feedPose)
        Logger.recordOutput("FeedThingAngle", FieldConstants.feedPose.rotation.degrees)
    }

    fun noteInAmpSideIntakeBeambreak() = !Beambreaks.getIntakeAmpSide()
    fun noteInSpeakerSideIntakeBeambreak() = !Beambreaks.getIntakeSpeakerSide()
    fun noteInCenterBeambreak() = !Beambreaks.getCenter()
    fun noteInAmpSideHopperBeambreak() = !Beambreaks.getHopperAmpSide()
    fun noteInSpeakerSideHopperBeambreak() = !Beambreaks.getHopperSpeakerSide()

    fun noteInHopperSide(side: MechanismSide) = if (side == MechanismSide.SPEAKER) noteInSpeakerSideHopperBeambreak() else noteInAmpSideHopperBeambreak()
    fun noteInIntakeSide(side: MechanismSide) = if (side == MechanismSide.SPEAKER) noteInSpeakerSideIntakeBeambreak() else noteInAmpSideIntakeBeambreak()

    fun noteInAnyHopper() = noteInAmpSideHopperBeambreak() || noteInSpeakerSideHopperBeambreak()
    fun noteInAnyIntake() = noteInAmpSideIntakeBeambreak() || noteInSpeakerSideIntakeBeambreak()
    fun noteInAnyBeambreak() = noteInAmpSideIntakeBeambreak() || noteInSpeakerSideIntakeBeambreak() || noteInCenterBeambreak() || noteInAmpSideHopperBeambreak() || noteInSpeakerSideHopperBeambreak()

    // This prioritizes the amp side, but it should be really hard to actually get a note in both
    fun getOneIntakeBeambreak(): MechanismSide? {
        return if (noteInAmpSideIntakeBeambreak()) MechanismSide.AMP
        else if (noteInSpeakerSideIntakeBeambreak()) MechanismSide.SPEAKER
        else null
    }

    fun getMovementDirection(): MechanismSide {
        val speeds = Drivetrain.getRobotRelativeSpeeds()
        if (speeds.vxMetersPerSecond > 0) return MechanismSide.SPEAKER else return MechanismSide.AMP
    }

    fun shouldRunOneIntake(): Boolean {
        val speeds = Drivetrain.getRobotRelativeSpeeds()
        return maxOf(abs(speeds.vxMetersPerSecond), abs(speeds.vyMetersPerSecond)) > 1
    }

    fun findNote() = when {
        noteInAmpSideIntakeBeambreak() -> NotePosition.AMP_INTAKE
        noteInSpeakerSideIntakeBeambreak() -> NotePosition.SPEAKER_INTAKE
        noteInAmpSideHopperBeambreak() -> NotePosition.AMP_HOPPER
        noteInSpeakerSideHopperBeambreak() -> NotePosition.SPEAKER_HOPPER
        else -> null
    }

    var notePosition = NotePosition.NONE

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
