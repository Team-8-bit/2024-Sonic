package org.team9432.robot.subsystems.hood

import edu.wpi.first.math.geometry.Rotation2d
import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.lib.commandbased.commands.SimpleCommand

/* Interface for interacting with the subsystem through command based systems */
object CommandHood {
    fun stop() = InstantCommand(Hood) { Hood.stop() }
    fun followAngle(angle: () -> Rotation2d) = SimpleCommand(
        requirements = setOf(Hood),
        execute = { Hood.setAngle(angle.invoke()) },
        end = { Hood.setAngle(Rotation2d()) }
    )
    fun setVoltage(volts: Double) = InstantCommand(Hood) { Hood.setVoltage(volts) }
    fun resetAngle() = InstantCommand(Hood) { Hood.resetAngle() }
}