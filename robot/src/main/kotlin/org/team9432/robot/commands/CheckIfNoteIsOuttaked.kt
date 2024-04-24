package org.team9432.robot.commands

import org.team9432.lib.commandbased.commands.*
import org.team9432.robot.RobotState

fun CheckIfNoteIsOuttaked() = InstantCommand { checkIfNoteIsOuttaked() }

fun checkIfNoteIsOuttaked() {
    if (!RobotState.noteInAnyBeambreak()) RobotState.notePosition = RobotState.NotePosition.NONE
}