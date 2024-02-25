package org.team9432.robot.subsystems.shooter

import edu.wpi.first.math.controller.SimpleMotorFeedforward
import org.littletonrobotics.junction.Logger
import org.team9432.Robot
import org.team9432.Robot.Mode.*

class ShooterSide(shooterSide: ShooterSideIO.ShooterSide) {
    private val io: ShooterSideIO
    private val inputs = LoggedShooterSideIOInputs()

    private val feedforward: SimpleMotorFeedforward

    init {
        when (Robot.mode) {
            REAL, REPLAY -> {
                io = ShooterSideIOVortex(shooterSide)
                io.setPID(0.0, 0.0, 0.0)
                feedforward = SimpleMotorFeedforward(0.0, 0.0)
            }

            SIM -> {
                io = ShooterSideIOSim(shooterSide)
                io.setPID(0.1, 0.0, 0.0)
                feedforward = SimpleMotorFeedforward(0.0, 0.0)
            }
        }
    }

    fun periodic() {
        io.updateInputs(inputs)
        Logger.processInputs("Shooter/${io.shooterSide.name}_Side", inputs)
    }

    fun setVolts(volts: Double) {
        io.setVoltage(volts)
    }

    fun setSpeed(rotationsPerMinute: Double) {
        io.setSpeed(rotationsPerMinute, feedforward.calculate(rotationsPerMinute))

        Logger.recordOutput("Shooter/${io.shooterSide.name}_Side/SetpointRPM", rotationsPerMinute)
    }

    fun stop() = io.stop()
}