package org.team9432.robot.commands.hopper

import org.team9432.lib.commandbased.KCommand
import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.lib.commandbased.commands.SequentialCommand
import org.team9432.lib.commandbased.commands.SuppliedCommand
import org.team9432.robot.MechanismSide.AMP
import org.team9432.robot.MechanismSide.SPEAKER
import org.team9432.robot.RobotState
import org.team9432.robot.RobotState.NotePosition.*

/**
 * Move a note to anywhere in the robot.
 * This moves the first step, then recursively calls itself until it gets to the right position.
 */
fun MoveToPosition(finalPosition: RobotState.NotePosition): KCommand = SuppliedCommand {
    // Don't move if it's already in the right position
    if (finalPosition == RobotState.notePosition) return@SuppliedCommand InstantCommand {}

    SequentialCommand(
        when {
            RobotState.notePosition == AMP_INTAKE && finalPosition == SPEAKER_INTAKE -> MoveFromIntakeToIntake(AMP, SPEAKER)
            RobotState.notePosition == AMP_INTAKE && finalPosition == AMP_HOPPER -> MoveFromIntakeToIntake(AMP, SPEAKER)
            RobotState.notePosition == AMP_INTAKE && finalPosition == SPEAKER_HOPPER -> MoveFromIntakeToHopper(AMP, SPEAKER)

            RobotState.notePosition == SPEAKER_INTAKE && finalPosition == AMP_INTAKE -> MoveFromIntakeToIntake(SPEAKER, AMP)
            RobotState.notePosition == SPEAKER_INTAKE && finalPosition == AMP_HOPPER -> MoveFromIntakeToHopper(SPEAKER, AMP)
            RobotState.notePosition == SPEAKER_INTAKE && finalPosition == SPEAKER_HOPPER -> MoveFromIntakeToIntake(SPEAKER, AMP)

            (RobotState.notePosition == AMP_HOPPER && finalPosition == AMP_INTAKE) ||
                    (RobotState.notePosition == AMP_HOPPER && finalPosition == SPEAKER_INTAKE) ||
                    (RobotState.notePosition == AMP_HOPPER && finalPosition == SPEAKER_HOPPER) -> MoveFromHopperToIntake(AMP, SPEAKER)

            (RobotState.notePosition == SPEAKER_HOPPER && finalPosition == AMP_INTAKE) ||
                    (RobotState.notePosition == SPEAKER_HOPPER && finalPosition == SPEAKER_INTAKE) ||
                    (RobotState.notePosition == SPEAKER_HOPPER && finalPosition == AMP_HOPPER) -> MoveFromHopperToIntake(SPEAKER, AMP)

            finalPosition == NONE -> throw Exception("this isn't a thing")
            else -> InstantCommand {}
        },
        MoveToPosition(finalPosition)
    )
}