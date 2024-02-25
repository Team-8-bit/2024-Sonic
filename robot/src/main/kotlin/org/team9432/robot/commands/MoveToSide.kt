package org.team9432.robot.commands

import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.lib.commandbased.commands.SequentialCommand
import org.team9432.lib.commandbased.commands.WaitUntilCommand
import org.team9432.robot.MechanismSide
import org.team9432.robot.RobotState
import org.team9432.robot.subsystems.hopper.Hopper
import org.team9432.robot.subsystems.intake.Intake
import java.lang.Exception

fun moveToSide(side: MechanismSide) = SequentialCommand(
    // This just starts the hopper in the right direction
    when {
        side == MechanismSide.SPEAKER && RobotState.noteInSpeakerSideIntake() -> InstantCommand { Hopper.runPercentage(0.5) } // Bend the note on the speaker side
        side == MechanismSide.AMP && RobotState.noteInAmpSideIntake() -> InstantCommand { Hopper.runPercentage(-0.5) } // Bend the note on the amp side
        side == MechanismSide.SPEAKER && RobotState.noteInAmpSideIntake() -> InstantCommand { Hopper.runPercentage(0.5) } // Send the note straight from amp side to speaker side
        side == MechanismSide.AMP && RobotState.noteInSpeakerSideIntake() -> InstantCommand { Hopper.runPercentage(-0.5) } // Send the note straight from speaker side to amp side
        else -> throw Exception() // Too tired
    },
    // Just run both intakes for now, though we don't really need to
    InstantCommand { Intake.runVolts(3.0, 3.0) },

    // Again, this just checks both sides
    WaitUntilCommand { !Hopper.ampSideBeambreakActive || !Hopper.speakerSideBeambreakActive },

    // Run back slowly to align the note. There is totally a better way to do this
    when {
        !Hopper.ampSideBeambreakActive -> InstantCommand { Hopper.runPercentage(0.2) }
        !Hopper.speakerSideBeambreakActive -> InstantCommand { Hopper.runPercentage(-0.2) }
        else -> throw Exception()
    },

    WaitUntilCommand { !Hopper.ampSideBeambreakActive && !Hopper.speakerSideBeambreakActive },
    InstantCommand { Hopper.stop() }
)