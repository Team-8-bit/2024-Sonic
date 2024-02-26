package org.team9432.robot.commands

import org.team9432.lib.commandbased.commands.*
import org.team9432.robot.MechanismSide
import org.team9432.robot.RobotState
import org.team9432.robot.subsystems.hopper.Hopper
import org.team9432.robot.subsystems.intake.Intake

fun moveToSide(side: MechanismSide) = SequentialCommand(
    // This just starts the hopper in the right direction
    when (side) {
        MechanismSide.SPEAKER -> InstantCommand { Hopper.setVoltage(10.0) }
        MechanismSide.AMP -> InstantCommand { Hopper.setVoltage(-10.0) }
    },

    // Wait a bit to get the hopper up to speed
    WaitCommand(0.5),

    // Both intakes need to be run when feeding across, but it could run only one when bending the note
    Intake.runVolts(-10.0, -10.0),

    WaitUntilCommand { RobotState.noteInHopperSide(side) },

    when (side) {
        MechanismSide.SPEAKER -> InstantCommand { Hopper.setVoltage(-3.0) }
        MechanismSide.AMP -> InstantCommand { Hopper.setVoltage(3.0) }
    },

    WaitUntilCommand { !RobotState.noteInHopperSide(side) },

    ParallelCommand(Hopper.stopCommand(), Intake.stopCommand())
)