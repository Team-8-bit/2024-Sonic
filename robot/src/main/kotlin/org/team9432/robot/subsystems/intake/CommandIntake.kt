package org.team9432.robot.subsystems.intake

import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.robot.MechanismSide

// TODO Make an annotation processor to generate this
/* Interface for interacting with the subsystem through command based systems */
object CommandIntake {
    fun setVoltage(ampSideVolts: Double, speakerSideVolts: Double) = InstantCommand(Intake) { Intake.setVoltage(ampSideVolts, speakerSideVolts) }
    fun stop() = InstantCommand(Intake) { Intake.stop() }
    fun runIntake(side: MechanismSide, volts: Double) = InstantCommand(Intake) { Intake.runIntake(side, volts) }
    fun setSpeedCommand(ampSideRPM: Double, speakerSideRPM: Double) = InstantCommand(Intake) { Intake.setSpeed(ampSideRPM, speakerSideRPM) }
}