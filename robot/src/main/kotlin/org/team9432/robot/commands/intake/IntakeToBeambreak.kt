package org.team9432.robot.commands.intake

import org.team9432.lib.commandbased.KCommand
import org.team9432.robot.MechanismSide
import org.team9432.robot.RobotState
import org.team9432.robot.RobotState.NotePosition
import org.team9432.robot.subsystems.intake.Intake

class IntakeToBeambreak: KCommand() {
    override val requirements = setOf(Intake)

    override fun execute() {
        // If the robot is moving fast, only run one intake
        if (RobotState.shouldRunOneIntake()) {
            when (RobotState.getMovementDirection()) {
                MechanismSide.SPEAKER -> Intake.setVoltage(0.0, 10.0)
                MechanismSide.AMP -> Intake.setVoltage(10.0, 0.0)
            }
        } else {
            Intake.setVoltage(10.0, 10.0)
        }
    }

    // End when a note hits the center beambreak
    override fun isFinished(): Boolean {
        return RobotState.noteInCenterBeambreak()
    }

    override fun end(interrupted: Boolean) {
        // If there is now a note in the intake, check which one and update the robot state accordingly
        if (RobotState.noteInCenterBeambreak()) {
            when {
                RobotState.noteInAmpSideIntakeBeambreak() -> RobotState.notePosition = NotePosition.AMP_INTAKE
                RobotState.noteInSpeakerSideIntakeBeambreak() -> RobotState.notePosition = NotePosition.SPEAKER_INTAKE
            }
        }

        Intake.stop()
    }
}
