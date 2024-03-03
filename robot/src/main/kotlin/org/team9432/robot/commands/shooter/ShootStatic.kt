package org.team9432.robot.commands.shooter

import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.lib.commandbased.commands.ParallelCommand
import org.team9432.lib.commandbased.commands.SequentialCommand
import org.team9432.lib.commandbased.commands.WaitCommand
import org.team9432.robot.MechanismSide
import org.team9432.robot.RobotState
import org.team9432.robot.commands.hopper.MoveToSide
import org.team9432.robot.subsystems.hopper.CommandHopper
import org.team9432.robot.subsystems.shooter.CommandShooter

fun ShootStatic(rpmLeft: Double, rpmRight: Double) = SequentialCommand(
    MoveToSide(MechanismSide.SPEAKER),
    CommandShooter.setSpeed(rpmLeft, rpmRight),
    WaitCommand(2.0),
    CommandHopper.loadTo(MechanismSide.SPEAKER, 5.0),
    WaitCommand(5.0),
    CommandShooter.stop(),
    CommandHopper.stop(),
    InstantCommand { RobotState.notePosition = RobotState.NotePosition.NONE }
)