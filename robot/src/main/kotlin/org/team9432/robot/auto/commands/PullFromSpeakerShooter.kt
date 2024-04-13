package org.team9432.robot.auto.commands

import org.team9432.lib.commandbased.commands.ParallelDeadlineCommand
import org.team9432.lib.commandbased.commands.WaitUntilCommand
import org.team9432.lib.commandbased.commands.afterSimDelay
import org.team9432.robot.MechanismSide
import org.team9432.robot.RobotState
import org.team9432.robot.sensors.beambreaks.BeambreakIOSim
import org.team9432.robot.subsystems.Superstructure

fun PullFromSpeakerShooter() = ParallelDeadlineCommand(
    Superstructure.Commands.runUnload(MechanismSide.SPEAKER),
    deadline = WaitUntilCommand { !RobotState.noteInHopperSide(MechanismSide.SPEAKER) }.afterSimDelay(0.25) { BeambreakIOSim.setNoteInHopperSide(MechanismSide.SPEAKER, false) }
)