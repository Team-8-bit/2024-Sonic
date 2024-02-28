package org.team9432.robot.commands.drivetrain

import org.team9432.lib.commandbased.KCommand
import org.team9432.robot.subsystems.drivetrain.Drivetrain

class FieldOrientedDrive: KCommand() {
    override val requirements = setOf(Drivetrain)

    override fun initialize() {
        Drivetrain.mode = Drivetrain.DrivetrainMode.MANUAL
    }

    override fun end(interrupted: Boolean) {
        Drivetrain.stop()
    }

    override fun isFinished() = false
}