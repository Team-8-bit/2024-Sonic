package org.team9432.robot.subsystems.climber

import edu.wpi.first.math.controller.SimpleMotorFeedforward
import edu.wpi.first.math.geometry.Rotation2d
import org.littletonrobotics.junction.Logger
import org.team9432.Robot
import org.team9432.Robot.Mode.*
import org.team9432.lib.commandbased.KSubsystem

object RightClimber: KSubsystem() {
    private val io: ClimberSideIO
    private val inputs = LoggedClimberSideIOInputs()

    private val feedforward: SimpleMotorFeedforward

    private var currentVoltage = 0.0

    init {
        when (Robot.mode) {
            REAL, REPLAY -> {
                io = ClimberSideIONeo(ClimberSideIO.ClimberSide.RIGHT)
                io.setPID(0.0, 0.0, 0.0)
                feedforward = SimpleMotorFeedforward(0.0, 0.0)
            }

            SIM -> {
                io = object: ClimberSideIO {
                    override val climberSide = ClimberSideIO.ClimberSide.RIGHT
                }
                io.setPID(0.0, 0.0, 0.0)
                feedforward = SimpleMotorFeedforward(0.0, 0.0)
            }
        }
    }

    override fun periodic() {
        io.updateInputs(inputs)
        Logger.processInputs("Climber/Right", inputs)
    }

    fun setVoltage(volts: Double) {
        currentVoltage = volts
        if (atLimit && volts < 0.0) {
            io.stop()
        } else {
            io.setVoltage(volts)
        }
    }

    fun setAngle(angle: Rotation2d) {
        io.setAngle(angle, feedforward.calculate(angle.degrees))
    }

    val atLimit get() = !inputs.limit
    val hasVoltageApplied get() = currentVoltage != 0.0

    fun stop() {
        currentVoltage = 0.0
        io.stop()
    }
}