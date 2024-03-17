package org.team9432.robot.commands

import org.team9432.robot.commands.drivetrain.teleop.TeleDrive
import org.team9432.robot.subsystems.drivetrain.Drivetrain

object DefaultCommands {
    fun setDefaultCommands() {
        Drivetrain.defaultCommand = TeleDrive()
    }
}