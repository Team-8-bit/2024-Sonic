package org.team9432.robot.commands.amp

import org.team9432.lib.commandbased.commands.ParallelCommand
import org.team9432.robot.MechanismSide
import org.team9432.robot.subsystems.Amp
import org.team9432.robot.subsystems.Superstructure

/** Outtakes from the amp side hopper. */
fun OuttakeAmp() = ParallelCommand(
    Amp.Commands.runVoltage(5.0),
    Superstructure.Commands.runLoad(MechanismSide.AMP)
)