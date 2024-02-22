package org.team9432.robot.subsystems.hood

import edu.wpi.first.math.controller.SimpleMotorFeedforward
import edu.wpi.first.math.geometry.Rotation2d
import org.littletonrobotics.junction.Logger
import org.team9432.Robot
import org.team9432.Robot.Mode.*
import org.team9432.lib.commandbased.KSubsystem

object Hood: KSubsystem() {
    private val io: HoodIO
    private val inputs = LoggedHoodIOInputs()

    private val feedforward: SimpleMotorFeedforward

    init {
        when (Robot.mode) {
            REAL, REPLAY -> {
                io = HoodIONeo()
                io.setPID(0.0, 0.0, 0.0)
                feedforward = SimpleMotorFeedforward(0.0, 0.0)
            }

            SIM -> {
                io = object: HoodIO {}
                feedforward = SimpleMotorFeedforward(0.0, 0.0)
            }
        }
    }

    override fun constantPeriodic() {
        io.updateInputs(inputs)
        Logger.processInputs("Hood", inputs)
    }

    fun runVolts(volts: Double) {
        io.setVoltage(volts)
    }

    fun setAngle(angle: Rotation2d) {
        io.setAngle(angle, feedforward.calculate(angle.degrees))

        Logger.recordOutput("Hood/AngleSetpointDegrees", angle.degrees)
    }

    fun stop() = io.stop()
}