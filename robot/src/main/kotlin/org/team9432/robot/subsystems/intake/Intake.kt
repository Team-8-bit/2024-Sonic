package org.team9432.robot.subsystems.intake

import edu.wpi.first.math.controller.SimpleMotorFeedforward
import org.littletonrobotics.junction.Logger
import org.team9432.Robot
import org.team9432.Robot.Mode.*
import org.team9432.lib.commandbased.KSubsystem
import org.team9432.lib.commandbased.commands.InstantCommand

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

    fun runVolts(ampSideVolts: Double, shooterSideVolts: Double) = InstantCommand(Intake) { io.setVoltage(ampSideVolts, shooterSideVolts) }

    fun stopCommand() = InstantCommand(Intake) { io.stop() }

    fun setSpeed(ampSideRPM: Double, shooterSideRPM: Double) = InstantCommand(Intake) {
        io.setSpeed(ampSideRPM, feedforward.calculate(ampSideRPM), shooterSideRPM, feedforward.calculate(shooterSideRPM))

        Logger.recordOutput("Intake/AmpSideSetpointRPM", ampSideRPM)
        Logger.recordOutput("Intake/ShooterSideSetpointRPM", shooterSideRPM)
    }

    val ampSideBeambreakActive get() = inputs.ampSideBeambreakActive
    val speakerSideBeambreakActive get() = inputs.centerBeambreakActive
    val centerBeambreakActive get() = inputs.centerBeambreakActive
}