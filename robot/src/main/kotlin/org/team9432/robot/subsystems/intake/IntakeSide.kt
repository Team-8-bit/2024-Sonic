package org.team9432.robot.subsystems.intake

import edu.wpi.first.math.controller.SimpleMotorFeedforward
import org.littletonrobotics.junction.Logger
import org.team9432.Robot
import org.team9432.Robot.Mode.*

class IntakeSide(intakeSide: IntakeSideIO.IntakeSide) {
    private val io: IntakeSideIO
    private val inputs = LoggedIntakeSideIOInputs()

    private val feedforward: SimpleMotorFeedforward

    init {
        when (Robot.mode) {
            REAL, REPLAY -> {
                io = IntakeSideIONeo(intakeSide)
                io.setPID(0.0, 0.0, 0.0)
                feedforward = SimpleMotorFeedforward(0.0, 0.0)
            }

            SIM -> {
                io = object: IntakeSideIO {
                    override val intakeSide = intakeSide
                }
                io.setPID(0.0, 0.0, 0.0)
                feedforward = SimpleMotorFeedforward(0.0, 0.0)
            }
        }
    }

    fun periodic() {
        io.updateInputs(inputs)
        Logger.processInputs("Intake/${io.intakeSide.name}_Side", inputs)
    }

    fun setVoltage(volts: Double) {
        io.setVoltage(volts)
    }

    fun setSpeed(rotationsPerMinute: Double) {
        io.setSpeed(rotationsPerMinute, feedforward.calculate(rotationsPerMinute))

        Logger.recordOutput("Intake/${io.intakeSide.name}/SetpointRPM", rotationsPerMinute)
    }

    fun stop() = io.stop()
}