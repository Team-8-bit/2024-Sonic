package org.team9432.robot.subsystems.hopper

import edu.wpi.first.math.controller.SimpleMotorFeedforward
import org.littletonrobotics.junction.Logger
import org.team9432.Robot
import org.team9432.Robot.Mode.*
import org.team9432.lib.commandbased.KSubsystem
import org.team9432.robot.MechanismSide

object Hopper : KSubsystem() {
    private val io: HopperIO
    private val inputs = LoggedHopperIOInputs()

    private val feedforward: SimpleMotorFeedforward

    init {
        when (Robot.mode) {
            REAL, REPLAY -> {
                io = HopperIOReal()
                io.setPID(0.0, 0.0, 0.0)
                feedforward = SimpleMotorFeedforward(0.0, 0.0)
            }

            SIM -> {
                io = HopperIOSim()
                io.setPID(0.0, 0.0, 0.0)
                feedforward = SimpleMotorFeedforward(0.0, 0.0)
            }
        }
    }

    override fun periodic() {
        io.updateInputs(inputs)
        Logger.processInputs("Hopper", inputs)
    }

    fun setVoltage(volts: Double) {
        io.setVoltage(volts)
    }

    fun loadTo(side: MechanismSide, volts: Double) =
        if (side == MechanismSide.SPEAKER) setVoltage(-volts) else setVoltage(volts)

    fun unloadFrom(side: MechanismSide, volts: Double) =
        if (side == MechanismSide.SPEAKER) setVoltage(volts) else setVoltage(-volts)

    fun setSpeed(rotationsPerMinute: Double) {
        io.setSpeed(rotationsPerMinute, feedforward.calculate(rotationsPerMinute))

        Logger.recordOutput("Hopper/SetpointRPM", rotationsPerMinute)
    }

    fun stop() = io.stop()
}