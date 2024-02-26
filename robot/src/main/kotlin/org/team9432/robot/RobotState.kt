package org.team9432.robot

import org.team9432.robot.subsystems.beambreaks.Beambreaks
import org.team9432.robot.subsystems.drivetrain.Drivetrain
import kotlin.math.abs

enum class MechanismSide { SPEAKER, AMP }

object RobotState {
    fun noteInAmpSideIntake() = !Beambreaks.getIntakeAmpSide()
    fun noteInSpeakerSideIntake() = !Beambreaks.getIntakeSpeakerSide()
    fun noteInCenter() = !Beambreaks.getHopperAmpSide()
    fun noteInAmpSideHopper() = !Beambreaks.getHopperSpeakerSide()
    fun noteInSpeakerSideHopper() = !Beambreaks.getCenter()

    fun noteInHopperSide(side: MechanismSide) = if (side == MechanismSide.SPEAKER) noteInSpeakerSideHopper() else noteInAmpSideHopper()
    fun noteInIntakeSide(side: MechanismSide) = if (side == MechanismSide.SPEAKER) noteInSpeakerSideIntake() else noteInAmpSideIntake()

    fun getMovementDirection(): MechanismSide {
        val speeds = Drivetrain.getRobotRelativeSpeeds()
        if (speeds.vxMetersPerSecond > 0) return MechanismSide.SPEAKER else return MechanismSide.AMP
    }

    fun shouldRunOneIntake(): Boolean {
        val speeds = Drivetrain.getRobotRelativeSpeeds()
        return maxOf(abs(speeds.vxMetersPerSecond), abs(speeds.vyMetersPerSecond)) > 1
    }
}