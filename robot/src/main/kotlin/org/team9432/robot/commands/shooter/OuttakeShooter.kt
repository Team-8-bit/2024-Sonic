package org.team9432.robot.commands.shooter

import org.team9432.lib.commandbased.commands.ParallelCommand
import org.team9432.robot.MechanismSide
import org.team9432.robot.subsystems.Shooter
import org.team9432.robot.subsystems.Superstructure

/** Outtake from the shooter side hopper. */
fun OuttakeShooter() = ParallelCommand(
    Shooter.Commands.runAtSpeeds(500.0, 500.0),
    Superstructure.Commands.runLoad(MechanismSide.SPEAKER)
)