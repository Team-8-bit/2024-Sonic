package org.team9432.robot.subsystems.amp

import edu.wpi.first.math.controller.SimpleMotorFeedforward
import org.littletonrobotics.junction.Logger
import org.team9432.Robot
import org.team9432.Robot.Mode.*
import org.team9432.lib.commandbased.KSubsystem

object Amp: KSubsystem() {
    private val io: AmpIO
    private val inputs = LoggedAmpIOInputs()

    private val feedforward: SimpleMotorFeedforward

    init {
        when (Robot.mode) {
            REAL, REPLAY -> {
                io = AmpIOReal()
                io.setPID(0.0, 0.0, 0.0)
                feedforward = SimpleMotorFeedforward(0.0, 0.0)
            }

            SIM -> {
                io = AmpIOSim()
                io.setPID(0.1, 0.0, 0.0)
                feedforward = SimpleMotorFeedforward(0.0, 0.0)
            }
        }
    }

    override fun constantPeriodic() {
        io.updateInputs(inputs)
        Logger.processInputs("Amp", inputs)
    }

    fun setSpeed(rpm: Double) {
        io.setSpeed(rpm, feedforward.calculate(rpm))

        Logger.recordOutput("Amp/SetpointRPM", rpm)
    }

    fun stop() = io.stop()
}