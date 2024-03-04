package org.team9432.robot.subsystems.intake

import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.robot.MechanismSide

// TODO Make an annotation processor to generate this
/* Interface for interacting with the subsystem through command based systems */
object CommandIntake {
    fun intake(ampVolts: Double, speakerVolts: Double) = InstantCommand(Intake) { Intake.intake(ampVolts, speakerVolts) }
    fun outtake(ampVolts: Double, speakerVolts: Double) = InstantCommand(Intake) { Intake.outtake(ampVolts, speakerVolts) }
    fun stop() = InstantCommand(Intake) { Intake.stop() }
    fun runTeleIntake(volts: Double) = InstantCommand(Intake) { Intake.runTeleIntake(volts) }
    fun intakeSide(side: MechanismSide, volts: Double) = InstantCommand(Intake) { Intake.intakeSide(side, volts) }
    fun outtakeSide(side: MechanismSide, volts: Double) = InstantCommand(Intake) { Intake.outtakeSide(side, volts) }
}