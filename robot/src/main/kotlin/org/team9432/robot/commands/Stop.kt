package org.team9432.robot.commands

import org.team9432.lib.commandbased.KCommandScheduler
import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.robot.RobotState
import org.team9432.robot.subsystems.Amp
import org.team9432.robot.subsystems.Hood
import org.team9432.robot.subsystems.Shooter
import org.team9432.robot.subsystems.Superstructure

fun stop() {
    KCommandScheduler.cancelAll()
    Superstructure.stop()
    Shooter.stop()
    Amp.stop()
    Hood.stop()
}

fun stopCommand() = InstantCommand { stop() }