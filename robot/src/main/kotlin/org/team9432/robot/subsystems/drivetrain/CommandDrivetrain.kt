package org.team9432.robot.subsystems.drivetrain

import org.team9432.lib.commandbased.commands.InstantCommand

/* Interface for interacting with the subsystem through command based systems */
object CommandDrivetrain {
    fun stop() = InstantCommand(Drivetrain) { Drivetrain.stop() }
    fun stopAndX() = InstantCommand(Drivetrain) { Drivetrain.stopAndX() }
}