package org.team9432.robot.subsystems.climber

import org.littletonrobotics.junction.Logger
import org.team9432.Robot
import org.team9432.Robot.Mode.*
import org.team9432.lib.commandbased.KSubsystem

object LeftClimber: KSubsystem() {
    private val io: ClimberSideIO
    private val inputs = LoggedClimberSideIOInputs()

    private var currentVoltage = 0.0

    init {
        io = when (Robot.mode) {
            REAL, REPLAY -> ClimberSideIONeo(ClimberSideIO.ClimberSide.LEFT)
            SIM -> object: ClimberSideIO {
                override val climberSide = ClimberSideIO.ClimberSide.LEFT
            }
        }
    }

    override fun periodic() {
        io.updateInputs(inputs)
        Logger.processInputs("Climber/Left", inputs)
    }

    fun setVoltage(volts: Double) {
        currentVoltage = volts
        if (atLimit && volts < 0.0) {
            io.stop()
        } else {
            io.setVoltage(volts)
        }
    }

    val atLimit get() = !inputs.limit
    val hasVoltageApplied get() = currentVoltage != 0.0

    fun stop() {
        currentVoltage = 0.0
        io.stop()
    }
}