package org.team9432.robot.subsystems.hopper

import org.littletonrobotics.junction.Logger
import org.team9432.Robot
import org.team9432.Robot.Mode.*
import org.team9432.lib.commandbased.KCommand
import org.team9432.lib.commandbased.KSubsystem
import org.team9432.lib.commandbased.commands.SimpleCommand
import org.team9432.robot.subsystems.hood.Hood

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

    fun runPercentage(percentage: Double) {
        io.setSpeed(percentage)
    }

    fun setVoltage(volts: Double) {
        io.setVoltage(volts)
    }

    val ampSideBeambreakActive get() = inputs.atAmpBeamBreak
    val speakerSideBeambreakActive get() = inputs.atShooterBeamBreak

    fun stopCommand(): KCommand {
        return SimpleCommand(
            initialize = { io.setSpeed(0.0) },
            isFinished = { true },
            requirements = mutableSetOf(Hopper)
        )
    }

    fun stop() = io.setVoltage(0.0)
}