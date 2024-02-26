package org.team9432.robot.subsystems.intake

import org.team9432.lib.commandbased.KSubsystem
import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.lib.commandbased.commands.SimpleCommand
import org.team9432.robot.MechanismSide
import org.team9432.robot.RobotState

object Intake: KSubsystem() {
    private val ampSide = IntakeSide(IntakeSideIO.IntakeSide.AMP)
    private val speakerSide = IntakeSide(IntakeSideIO.IntakeSide.SPEAKER)

    override fun constantPeriodic() {
        ampSide.periodic()
        speakerSide.periodic()
    }

    fun setVoltage(ampSideVolts: Double, speakerSideVolts: Double) = InstantCommand(Intake) {
        ampSide.setVoltage(ampSideVolts)
        speakerSide.setVoltage(speakerSideVolts)
    }

    fun teleIntakeCommand() = SimpleCommand(
        execute = {
            if (RobotState.shouldRunOneIntake()) {
                when (RobotState.getMovementDirection()) {
                    MechanismSide.SPEAKER -> {
                        speakerSide.setVoltage(10.0); ampSide.setVoltage(0.0)
                    }

                    MechanismSide.AMP -> {
                        ampSide.setVoltage(10.0); speakerSide.setVoltage(0.0)
                    }
                }
            } else {
                speakerSide.setVoltage(10.0)
                ampSide.setVoltage(10.0)
            }
        },

        requirements = setOf(Intake)
    )

    fun stopCommand() = InstantCommand(Intake) {
        ampSide.stop()
        speakerSide.stop()
    }

    fun runIntake(side: MechanismSide, volts: Double) = if (side == MechanismSide.SPEAKER) {
        InstantCommand(Intake) { speakerSide.setVoltage(volts) }
    } else {
        InstantCommand(Intake) { ampSide.setVoltage(volts) }
    }


    fun setSpeed(ampSideRPM: Double, speakerSideRPM: Double) = InstantCommand(Intake) {
        ampSide.setSpeed(ampSideRPM)
        speakerSide.setSpeed(speakerSideRPM)
    }
}