package org.team9432.robot.commands.intake

import org.team9432.lib.commandbased.commands.*
import org.team9432.robot.RobotState
import org.team9432.robot.subsystems.beambreaks.BeambreakIOSim
import org.team9432.robot.subsystems.intake.CommandIntake
import org.team9432.robot.subsystems.intake.Intake
import org.team9432.robot.subsystems.led.LEDState

fun FinishIntakingAndAlign() = SuppliedCommand(Intake) {
    val side = RobotState.getOneIntakeBeambreak() ?: return@SuppliedCommand InstantCommand {}

    SequentialCommand(
        // Intake slowly until the note is fully in the intake
        CommandIntake.startIntakeSide(side, 4.0),
        WaitUntilCommand { RobotState.noteInCenterBeambreak() }.afterSimDelay(0.25) { BeambreakIOSim.setNoteInCenter(true) },
        // Push the note back into the intake beam break to leave more room before the hopper
        CommandIntake.startOuttakeSide(side, 4.0),
        WaitUntilCommand { !RobotState.noteInCenterBeambreak() }.afterSimDelay(0.25) { BeambreakIOSim.setNoteInCenter(false) },
        // Stop the intake
        CommandIntake.stop(),
        // Update the note position in the robot
        InstantCommand { RobotState.notePosition = side.getNotePositionIntake() },

        InstantCommand {
            SequentialCommand(
                WaitCommand(3.0),
                InstantCommand { LEDState.intakeLightOn = false }
            ).schedule()
        }
    )
}