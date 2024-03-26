package org.team9432.robot.sensors.beambreaks

import org.littletonrobotics.junction.Logger
import org.team9432.lib.State
import org.team9432.lib.State.Mode.*
import org.team9432.lib.commandbased.KPeriodic
import org.team9432.lib.commandbased.KSubsystem

object Beambreaks: KPeriodic() {
    private val io: BeambreakIO
    private val inputs = LoggedBeambreakIOInputs()

    init {
        when (State.mode) {
            REAL, REPLAY -> io = BeambreakIOReal()
            SIM -> io = BeambreakIOSim
        }
    }

    override fun periodic() {
        io.updateInputs(inputs)
        Logger.processInputs("Beambreaks", inputs)
    }

    fun getIntakeAmpSide() = inputs.intakeAmpSideActive
    fun getIntakeSpeakerSide() = inputs.intakeSpeakerSideActive
    fun getHopperAmpSide() = inputs.hopperAmpSideActive
    fun getHopperSpeakerSide() = inputs.hopperSpeakerSideActive
    fun getCenter() = inputs.centerActive
}