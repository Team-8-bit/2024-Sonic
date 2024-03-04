package org.team9432.robot.subsystems.intake

import org.littletonrobotics.junction.Logger
import org.team9432.Robot
import org.team9432.Robot.Mode.*

class IntakeSide(intakeSide: IntakeSideIO.IntakeSide) {
    private val io: IntakeSideIO
    private val inputs = LoggedIntakeSideIOInputs()

    init {
        io = when (Robot.mode) {
            REAL, REPLAY -> IntakeSideIONeo(intakeSide)
            SIM -> IntakeSideIOSim(intakeSide)
        }
    }

    fun periodic() {
        io.updateInputs(inputs)
        Logger.processInputs("Intake/${io.intakeSide.name}_Side", inputs)
    }

    fun setVoltage(volts: Double) {
        io.setVoltage(volts)
    }

    fun stop() = io.stop()
}