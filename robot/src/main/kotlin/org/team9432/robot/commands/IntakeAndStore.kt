package org.team9432.robot.commands

import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.lib.commandbased.commands.SequentialCommand
import org.team9432.lib.commandbased.commands.WaitUntilCommand
import org.team9432.robot.subsystems.intake.Intake

// Loads a note up to the center, then unloads it slightly to align it
val intakeAndStore
    get() = SequentialCommand(
        InstantCommand { Intake.runVolts(3.0, 3.0) }, // I have no clue how fast a volt is, but I don't want to tune pid
        WaitUntilCommand { Intake.centerBeambreakActive },
        InstantCommand { Intake.runVolts(-1.0, -1.0) },
        WaitUntilCommand { !Intake.centerBeambreakActive },
        InstantCommand { Intake.stop() }
    )