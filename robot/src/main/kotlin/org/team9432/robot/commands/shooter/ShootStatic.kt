package org.team9432.robot.commands.shooter

import org.team9432.lib.commandbased.commands.ParallelCommand
import org.team9432.robot.MechanismSide
import org.team9432.robot.commands.hopper.MoveToSide

fun ShootStatic(rpmLeft: Double, rpmRight: Double) = ParallelCommand(
    MoveToSide(MechanismSide.SPEAKER),
    PrepareSpeakerShot(),
    ShootFromHopper(rpmLeft, rpmRight),
)