package org.team9432.robot.subsystems.shooter

import edu.wpi.first.math.controller.SimpleMotorFeedforward
import org.littletonrobotics.junction.Logger
import org.team9432.Robot
import org.team9432.Robot.Mode.*
import org.team9432.lib.commandbased.KSubsystem

object Shooter: KSubsystem() {
    private val io: ShooterIO
    private val inputs = LoggedShooterIOInputs()

    private val feedforward: SimpleMotorFeedforward

    init {
        when (Robot.mode) {
            REAL, REPLAY -> {
                io = ShooterIOVortex()
                io.setPID(0.0, 0.0, 0.0)
                feedforward = SimpleMotorFeedforward(0.0, 0.0)
            }

            SIM -> {
                io = ShooterIOSim()
                io.setPID(0.1, 0.0, 0.0)
                feedforward = SimpleMotorFeedforward(0.0, 0.0)
            }
        }
    }

    override fun constantPeriodic() {
        io.updateInputs(inputs)
        Logger.processInputs("Shooter", inputs)
    }

    fun setSpeed(leftRPM: Double, rightRPM: Double) {
        io.setSpeed(leftRPM, feedforward.calculate(leftRPM), rightRPM, feedforward.calculate(rightRPM))

        Logger.recordOutput("Shooter/LeftSetpointRPM", leftRPM)
        Logger.recordOutput("Shooter/RightSetpointRPM", rightRPM)
    }

    fun setVolts(leftRPM: Double, rightRPM: Double) {
        io.setVoltage(leftRPM, rightRPM)
    }

    fun stop() = io.stop()
}