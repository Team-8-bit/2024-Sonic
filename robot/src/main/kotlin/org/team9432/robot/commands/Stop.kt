package org.team9432.robot.commands

import org.team9432.lib.commandbased.KCommandScheduler
import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.robot.RobotState
import org.team9432.robot.subsystems.Amp
import org.team9432.robot.subsystems.Hood
import org.team9432.robot.subsystems.Shooter
import org.team9432.robot.subsystems.Superstructure

/** Stops all motors, cancels all commands, and resets the note position in the robot. */
fun stop() {
    // If the robot KNOWS where the note is, update its position
    RobotState.findNote()?.let { RobotState.notePosition = it }

    KCommandScheduler.cancelAll()
    Superstructure.stop()
    Shooter.stop()
    Amp.stop()
    Hood.stop()
}

/** Stops all motors, cancels all commands, and resets the note position in the robot. */
fun stopCommand() = InstantCommand { stop() }