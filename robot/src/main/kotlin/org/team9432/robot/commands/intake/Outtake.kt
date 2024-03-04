package org.team9432.robot.commands.intake

import org.team9432.lib.commandbased.commands.SimpleCommand
import org.team9432.robot.RobotState
import org.team9432.robot.subsystems.intake.Intake

fun Outtake() = SimpleCommand(
    requirements = setOf(Intake),
    initialize = { Intake.outtake(8.0, 8.0) },
    end = {
        Intake.stop()
        if (!RobotState.noteInAnyIntake()) RobotState.notePosition = RobotState.NotePosition.NONE
    }
)