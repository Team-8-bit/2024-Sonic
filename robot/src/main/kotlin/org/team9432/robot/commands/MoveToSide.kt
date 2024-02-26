package org.team9432.robot.commands

import org.team9432.lib.commandbased.commands.*
import org.team9432.robot.MechanismSide
import org.team9432.robot.RobotState
import org.team9432.robot.subsystems.hopper.Hopper
import org.team9432.robot.subsystems.intake.Intake

fun MoveToSide(side: MechanismSide) = SequentialCommand(
    Hopper.loadTo(side, volts = 10.0),

    // Wait a bit to get the hopper up to speed
    WaitCommand(0.5),

    // Both intakes need to be run when feeding across, but it could run only one when bending the note
    Intake.setVoltageCommand(-10.0, -10.0),

    // After the note is at the beam break, slowly unload to align it
    WaitUntilCommand { RobotState.noteInHopperSide(side) },
    Intake.setVoltageCommand(3.0, 3.0),
    Hopper.unloadFrom(side, volts = 3.0),
    WaitUntilCommand { !RobotState.noteInHopperSide(side) },

    ParallelCommand(Hopper.stopCommand(), Intake.stopCommand())
)