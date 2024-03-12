package org.team9432.robot.auto.commands

import org.team9432.lib.commandbased.KCommand
import org.team9432.lib.commandbased.commands.ParallelCommand
import org.team9432.lib.commandbased.commands.SequentialCommand
import org.team9432.lib.commandbased.commands.WaitCommand
import org.team9432.robot.MechanismSide
import org.team9432.robot.commands.hopper.MoveToSide
import org.team9432.robot.commands.intake.FinishIntakingAndAlign

fun FinishIntakingThen(command: KCommand, delay: Double = 0.5) = ParallelCommand(
    SequentialCommand(
        WaitCommand(delay),
        command
    ),
    FinishIntakingAndLoadToSpeaker()
)

fun FinishIntakingAndLoadToSpeaker() = SequentialCommand(
    FinishIntakingAndAlign(),
    MoveToSide(MechanismSide.SPEAKER)
)