package org.team9432.robot.subsystems.hopper

import org.littletonrobotics.junction.Logger
import org.team9432.Robot
import org.team9432.Robot.Mode.*
import org.team9432.lib.commandbased.KSubsystem
import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.robot.subsystems.intake.Intake

object Hopper: KSubsystem() {
    private val io: HopperIO
    private val inputs = LoggedHopperIOInputs()

    init {
        when (Robot.mode) {
            REAL, REPLAY -> {
                io = HopperIOReal()
            }

            SIM -> {
                io = object: HopperIO {}
            }
        }
    }

    override fun constantPeriodic() {
        io.updateInputs(inputs)
        Logger.processInputs("Hopper", inputs)
    }

    fun setVoltage(volts: Double) = InstantCommand(mutableSetOf(Hopper)) { io.setVoltage(volts) }
    fun stopCommand() = InstantCommand(mutableSetOf(Hopper)) { io.stop() }

    val ampSideBeambreakActive get() = inputs.atAmpBeamBreak
    val speakerSideBeambreakActive get() = inputs.atShooterBeamBreak
}