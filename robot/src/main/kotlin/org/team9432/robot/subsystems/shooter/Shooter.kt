package org.team9432.robot.subsystems.shooter

import edu.wpi.first.math.controller.SimpleMotorFeedforward
import org.littletonrobotics.junction.Logger
import org.team9432.Robot
import org.team9432.Robot.Mode.*
import org.team9432.lib.commandbased.KSubsystem

class Shooter: KSubsystem() {
    private val io: ShooterIO
    private val inputs = LoggedShooterIOInputs()

    private val feedforward: SimpleMotorFeedforward

    init {
        when (Robot.mode) {
            REAL, REPLAY -> {
                io = object: ShooterIO {}
                io.setPID(0.0, 0.0, 0.0)
                feedforward = SimpleMotorFeedforward(0.0, 0.0)
            }

            SIM -> {
                io = object: ShooterIO {}
                io.setPID(1.0, 0.0, 0.0)
                feedforward = SimpleMotorFeedforward(0.0, 0.0)
            }
        }
    }

    override fun constantPeriodic() {
        io.updateInputs(inputs)
        Logger.processInputs("Shooter", inputs)
    }

    fun runVolts(volts: Double) {
        io.setVoltage(volts)
    }

    fun setSpeed(rotationsPerMinute: Double) {
        io.setSpeed(rotationsPerMinute, feedforward.calculate(rotationsPerMinute))

        Logger.recordOutput("Shooter/SetpointRPM", rotationsPerMinute)
    }

    fun stop() = io.stop()
}