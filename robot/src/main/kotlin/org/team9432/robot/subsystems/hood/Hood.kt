package org.team9432.robot.subsystems.hood

import edu.wpi.first.math.controller.SimpleMotorFeedforward
import edu.wpi.first.math.geometry.Pose3d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.geometry.Rotation3d
import edu.wpi.first.math.geometry.Translation3d
import org.littletonrobotics.junction.Logger
import org.team9432.Robot
import org.team9432.Robot.Mode.*
import org.team9432.lib.commandbased.KCommand
import org.team9432.lib.commandbased.KSubsystem
import org.team9432.lib.commandbased.commands.SimpleCommand
import org.team9432.robot.subsystems.hopper.Hopper
import org.team9432.robot.subsystems.intake.Intake

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
                io = HoodIOSim()
                io.setPID(1.0, 0.0, 0.0)
                feedforward = SimpleMotorFeedforward(0.0, 0.0)
            }
        }
    }

    override fun constantPeriodic() {
        io.updateInputs(inputs)
        Logger.processInputs("Hood", inputs)

        Logger.recordOutput("Subsystems/Hood", Pose3d(Translation3d(0.266700, 0.0, 0.209550 + 0.124460), Rotation3d(0.0, inputs.absoluteAngle.radians, 0.0)))
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