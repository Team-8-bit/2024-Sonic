package org.team9432.robot.commands

import org.team9432.lib.commandbased.commands.*
import org.team9432.robot.MechanismSide
import org.team9432.robot.RobotState
import org.team9432.robot.subsystems.intake.Intake

// Loads a note up to the center, then unloads it slightly to align it
fun intakeAndStore() = SequentialCommand(
    ParallelRaceCommand(
        Intake.teleIntakeCommand(),
        WaitUntilCommand { RobotState.noteInAmpSideIntake() || RobotState.noteInSpeakerSideIntake() }
    ),

    if (RobotState.noteInAmpSideIntake()) {
        SequentialCommand(
            Intake.runIntake(MechanismSide.AMP, -10.0),
            WaitUntilCommand { RobotState.noteInCenter() },
            Intake.runIntake(MechanismSide.AMP, 4.0),
            WaitUntilCommand { !RobotState.noteInCenter() }
        ).withTimeout(3.0)
    } else if (RobotState.noteInSpeakerSideIntake()) {
        SequentialCommand(
            Intake.runIntake(MechanismSide.SPEAKER, -10.0),
            WaitUntilCommand { RobotState.noteInCenter() },
            Intake.runIntake(MechanismSide.SPEAKER, 4.0),
            WaitUntilCommand { !RobotState.noteInCenter() }
        ).withTimeout(3.0)
    } else InstantCommand {},

    Intake.stopCommand()
)