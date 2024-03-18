package org.team9432.robot.subsystems.intake

import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.lib.commandbased.commands.SimpleCommand
import org.team9432.robot.MechanismSide

// TODO Make an annotation processor to generate this
/* Interface for interacting with the subsystem through command based systems */
object CommandIntake {
    fun startIntake(ampVolts: Double, speakerVolts: Double) = InstantCommand(Intake) { Intake.intake(ampVolts, speakerVolts) }
    fun startOuttake(ampVolts: Double, speakerVolts: Double) = InstantCommand(Intake) { Intake.outtake(ampVolts, speakerVolts) }
    fun stop() = InstantCommand(Intake) { Intake.stop() }
    fun startTeleIntake(volts: Double) = InstantCommand(Intake) { Intake.runTeleIntake(volts) }
    fun startIntakeSide(side: MechanismSide, volts: Double) = InstantCommand(Intake) { Intake.intakeSide(side, volts) }
    fun startOuttakeSide(side: MechanismSide, volts: Double) = InstantCommand(Intake) { Intake.outtakeSide(side, volts) }

    fun runTeleIntake(volts: Double) = SimpleCommand(
        requirements = setOf(Intake),
        execute = { Intake.runTeleIntake(volts) },
        end = { Intake.stop() }
    )

    fun runIntakeSide(side: MechanismSide, volts: Double) = SimpleCommand(
        requirements = setOf(Intake),
        execute = { Intake.intakeSide(side, volts) },
        end = { Intake.stop() }
    )

    fun runOuttakeSide(side: MechanismSide, volts: Double) = SimpleCommand(
        requirements = setOf(Intake),
        execute = { Intake.outtakeSide(side, volts) },
        end = { Intake.stop() }
    )
}