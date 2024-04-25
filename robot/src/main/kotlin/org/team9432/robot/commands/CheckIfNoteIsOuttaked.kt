package org.team9432.robot.commands

import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.robot.RobotState

/** Check if there isn't a note detected by any beambreak, and set [RobotState.notePosition] to NONE if there isn't. */
fun CheckIfNoteIsOuttaked() = InstantCommand { checkIfNoteIsOuttaked() }

/** Check if there isn't a note detected by any beambreak, and set [RobotState.notePosition] to NONE if there isn't. */
fun checkIfNoteIsOuttaked() {
    if (!RobotState.noteInAnyBeambreak()) RobotState.notePosition = RobotState.NotePosition.NONE
}