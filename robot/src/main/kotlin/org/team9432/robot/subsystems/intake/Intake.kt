package org.team9432.robot.subsystems.intake

import edu.wpi.first.math.controller.SimpleMotorFeedforward
import org.littletonrobotics.junction.Logger
import org.team9432.Robot
import org.team9432.Robot.Mode.*
import org.team9432.lib.commandbased.KCommand
import org.team9432.lib.commandbased.KSubsystem
import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.lib.commandbased.commands.SimpleCommand

object Intake: KSubsystem() {
    private val io: IntakeIO
    private val inputs = LoggedIntakeIOInputs()

    private val feedforward: SimpleMotorFeedforward

    init {
        when (Robot.mode) {
            REAL, REPLAY -> {
                io = IntakeIONeo()
                io.setPID(0.0, 0.0, 0.0)
                feedforward = SimpleMotorFeedforward(0.0, 0.0)
            }

            SIM -> {
                io = object: IntakeIO {}
                io.setPID(0.0, 0.0, 0.0)
                feedforward = SimpleMotorFeedforward(0.0, 0.0)
            }
        }
    }

    override fun constantPeriodic() {
        io.updateInputs(inputs)
        Logger.processInputs("Intake", inputs)
    }

    fun runVolts(ampSideVolts: Double, shooterSideVolts: Double) {
        io.setVoltage(ampSideVolts, shooterSideVolts)
    }

    fun runIntake(ampSideVolts: Double, shooterSideVolts: Double): KCommand {
        return SimpleCommand(
            initialize = { io.setVoltage(ampSideVolts, shooterSideVolts) },
            end = { io.stop() },
            requirements = mutableSetOf(Intake)
        )
    }

    fun stopCommand(): KCommand {
        return SimpleCommand(
            initialize = { io.setVoltage(0.0, 0.0) },
            isFinished = { true },
            requirements = mutableSetOf(Intake)
        )
    }

    fun setSpeed(ampSideRPM: Double, shooterSideRPM: Double) {
        io.setSpeed(ampSideRPM, feedforward.calculate(ampSideRPM), shooterSideRPM, feedforward.calculate(shooterSideRPM))

        Logger.recordOutput("Intake/AmpSideSetpointRPM", ampSideRPM)
        Logger.recordOutput("Intake/ShooterSideSetpointRPM", shooterSideRPM)
    }

    val ampSideBeambreakActive get() = inputs.ampSideBeambreakActive
    val speakerSideBeambreakActive get() = inputs.centerBeambreakActive
    val centerBeambreakActive get() = inputs.centerBeambreakActive

    fun stop() = io.stop()
}