package org.team9432.robot.commands

import org.team9432.lib.commandbased.KCommandScheduler
import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.robot.RobotState
import org.team9432.robot.subsystems.*

fun stop() {
    RobotState.notePosition = RobotState.NotePosition.NONE
    KCommandScheduler.cancelAll()
    Intake.stop()
    Hopper.stop()
    Shooter.stop()
    Amp.stop()
    Hood.stop()
}

fun stopCommand() = InstantCommand { stop() }