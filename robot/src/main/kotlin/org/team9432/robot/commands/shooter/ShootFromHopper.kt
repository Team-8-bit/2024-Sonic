package org.team9432.robot.commands.shooter

import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.lib.commandbased.commands.SequentialCommand
import org.team9432.lib.commandbased.commands.WaitCommand
import org.team9432.robot.MechanismSide
import org.team9432.robot.RobotState
import org.team9432.robot.subsystems.hopper.CommandHopper
import org.team9432.robot.subsystems.shooter.CommandShooter

fun ShootFromHopper(rpmLeft: Double, rpmRight: Double) = SequentialCommand(
    CommandShooter.setSpeed(rpmLeft, rpmRight),
    WaitCommand(2.0), //TODO wait until the shooter is up to speed
    CommandHopper.loadTo(MechanismSide.SPEAKER, 5.0),
    WaitCommand(1.0),
    CommandShooter.stop(),
    CommandHopper.stop(),
    InstantCommand { RobotState.notePosition = RobotState.NotePosition.NONE }
)
