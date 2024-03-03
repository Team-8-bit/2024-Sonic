package org.team9432.robot.commands.amp

import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.lib.commandbased.commands.SequentialCommand
import org.team9432.lib.commandbased.commands.WaitCommand
import org.team9432.robot.MechanismSide
import org.team9432.robot.RobotState
import org.team9432.robot.commands.hopper.MoveToSide
import org.team9432.robot.subsystems.amp.CommandAmp
import org.team9432.robot.subsystems.hopper.CommandHopper

fun AmpShoot() = SequentialCommand(
    MoveToSide(MechanismSide.AMP),
    CommandAmp.setSpeed(1000.0), //TODO: Change to actual speed
    WaitCommand(1.0),
    CommandHopper.loadTo(MechanismSide.AMP, 5.0),
    WaitCommand(5.0),
    CommandAmp.stop(),
    CommandHopper.stop(),
    InstantCommand { RobotState.notePosition = RobotState.NotePosition.NONE }
)