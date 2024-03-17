package org.team9432.robot.subsystems.climber

import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.lib.commandbased.commands.SimpleCommand

object CommandClimber {
    fun runLeftClimber(volts: Double) = SimpleCommand(
        requirements = setOf(LeftClimber),
        execute = { LeftClimber.setVoltage(volts) },
        end = { LeftClimber.stop() }
    )

    fun runRightClimber(volts: Double) = SimpleCommand(
        requirements = setOf(RightClimber),
        execute = { RightClimber.setVoltage(volts) },
        end = { RightClimber.stop() }
    )

    fun runClimbers(volts: Double) = SimpleCommand(
        requirements = setOf(LeftClimber, RightClimber),
        execute = { LeftClimber.setVoltage(volts); RightClimber.setVoltage(volts) },
        end = { LeftClimber.stop(); RightClimber.stop() }
    )

    fun stop() = InstantCommand(LeftClimber, RightClimber) { LeftClimber.stop(); RightClimber.stop() }
}