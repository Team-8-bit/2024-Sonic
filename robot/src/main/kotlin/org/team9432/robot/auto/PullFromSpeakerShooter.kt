package org.team9432.robot.auto

import org.team9432.lib.commandbased.commands.ParallelDeadlineCommand
import org.team9432.lib.commandbased.commands.WaitUntilCommand
import org.team9432.lib.commandbased.commands.afterSimDelay
import org.team9432.robot.MechanismSide
import org.team9432.robot.RobotState
import org.team9432.robot.subsystems.beambreaks.BeambreakIOSim
import org.team9432.robot.subsystems.hopper.CommandHopper
import org.team9432.robot.subsystems.intake.CommandIntake

fun PullFromSpeakerShooter() = ParallelDeadlineCommand(
    CommandIntake.runOuttakeSide(MechanismSide.SPEAKER, 2.0),
    CommandHopper.runUnloadFrom(MechanismSide.SPEAKER, 2.0),
    deadline = WaitUntilCommand { !RobotState.noteInHopperSide(MechanismSide.SPEAKER) }.afterSimDelay(0.25) { BeambreakIOSim.setNoteInHopperSide(MechanismSide.SPEAKER, false) }
)