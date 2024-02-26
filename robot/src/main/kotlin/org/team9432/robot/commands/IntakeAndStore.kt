package org.team9432.robot.commands

import org.team9432.lib.commandbased.commands.SequentialCommand
import org.team9432.lib.commandbased.commands.afterSimDelay
import org.team9432.robot.MechanismSide
import org.team9432.robot.RobotState
import org.team9432.robot.commands.intake.AlignNote
import org.team9432.robot.commands.intake.IntakeToBeambreak
import org.team9432.robot.subsystems.beambreaks.BeambreakIOSim
import org.team9432.robot.subsystems.intake.Intake

// Loads a note up to the center, then unloads it slightly to align it
fun IntakeAndStore() = SequentialCommand(
    IntakeToBeambreak().afterSimDelay(3.0) {
        // Pretend to get a note after 3 seconds in sim
        if (RobotState.getMovementDirection() == MechanismSide.AMP) BeambreakIOSim.intakeAmpSide = false else BeambreakIOSim.intakeSpeakerSide = false
        BeambreakIOSim.center = false
    },
    AlignNote(),
    Intake.stopCommand()
)