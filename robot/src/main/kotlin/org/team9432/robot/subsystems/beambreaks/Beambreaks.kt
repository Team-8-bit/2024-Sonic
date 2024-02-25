package org.team9432.robot.subsystems.beambreaks

import org.littletonrobotics.junction.Logger
import org.team9432.Robot
import org.team9432.Robot.Mode.*
import org.team9432.lib.commandbased.KSubsystem

object Beambreaks: KSubsystem() {
    private val io: BeambreakIO
    private val inputs = LoggedBeambreakIOInputs()

    init {
        when (Robot.mode) {
            REAL, REPLAY -> io = BeambreakIOReal()
            SIM -> io = object: BeambreakIO {}
        }
    }

    override fun constantPeriodic() {
        io.updateInputs(inputs)
        Logger.processInputs("Beambreaks", inputs)
    }

    fun getIntakeAmpSide() = inputs.intakeAmpSideActive
    fun getIntakeSpeakerSide() = inputs.intakeSpeakerSideActive
    fun getHopperAmpSide() = inputs.hopperAmpSideActive
    fun getHopperSpeakerSide() = inputs.hopperSpeakerSideActive
    fun getCenter() = inputs.centerActive
}