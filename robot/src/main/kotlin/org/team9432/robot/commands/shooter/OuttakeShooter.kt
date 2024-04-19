package org.team9432.robot.commands.shooter

import org.team9432.lib.commandbased.commands.*
import org.team9432.robot.MechanismSide
import org.team9432.robot.subsystems.Shooter
import org.team9432.robot.subsystems.Superstructure

fun OuttakeShooter() = ParallelCommand(
    Shooter.Commands.runAtOuttakeSpeeds(),
    Superstructure.Commands.runLoad(MechanismSide.SPEAKER)
)