package org.team9432.robot.commands

import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.lib.commandbased.commands.SequentialCommand
import org.team9432.lib.commandbased.commands.WaitUntilCommand
import org.team9432.robot.MechanismSide
import org.team9432.robot.subsystems.hopper.Hopper
import org.team9432.robot.subsystems.intake.Intake

fun moveToSide(side: MechanismSide) = SequentialCommand(
    // This just starts the hopper in the right direction
    when (side) {
        MechanismSide.SPEAKER -> InstantCommand { Hopper.setVoltage(10.0) }
        MechanismSide.AMP -> InstantCommand { Hopper.setVoltage(-10.0) }
    },

    // Just run both intakes for now, though we don't really need to
    InstantCommand { Intake.runVolts(-10.0, -10.0) },

    // Again, this just checks both sides
    WaitUntilCommand { Hopper.ampSideBeambreakActive || Hopper.speakerSideBeambreakActive },

    // Run back slowly to align the note
    when (side) {
        MechanismSide.SPEAKER -> InstantCommand { Hopper.setVoltage(-3.0) }
        MechanismSide.AMP -> InstantCommand { Hopper.setVoltage(3.0) }
    },

    WaitUntilCommand { !Hopper.ampSideBeambreakActive && !Hopper.speakerSideBeambreakActive },
    InstantCommand { Hopper.stop(); Intake.stop() }
)