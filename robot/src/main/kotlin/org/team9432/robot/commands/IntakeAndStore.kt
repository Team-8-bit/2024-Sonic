package org.team9432.robot.commands

import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.lib.commandbased.commands.SequentialCommand
import org.team9432.lib.commandbased.commands.WaitUntilCommand
import org.team9432.robot.RobotState
import org.team9432.robot.subsystems.intake.Intake

// Loads a note up to the center, then unloads it slightly to align it
fun intakeAndScore() = SequentialCommand(
        Intake.runVolts(-10.0, -10.0), // I have no clue how fast a volt is, but I don't want to tune pid
        WaitUntilCommand { RobotState.noteInCenter() },
        Intake.runVolts(4.0, 4.0),
        WaitUntilCommand { !RobotState.noteInCenter() },
        Intake.stopCommand()
)