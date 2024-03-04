package org.team9432.robot.commands.intake

import org.team9432.lib.commandbased.KCommand
import org.team9432.robot.MechanismSide
import org.team9432.robot.RobotState
import org.team9432.robot.RobotState.NotePosition
import org.team9432.robot.subsystems.intake.Intake

class IntakeToBeambreak: KCommand() {
    val intakingVolts = 5.0
    var lastSide: MechanismSide? = null

    override var requirements = setOf(Intake)

    override fun initialize() {
        lastSide = null
    }

    override fun execute() {
        // If the robot is moving fast, only run one intake
        if (RobotState.shouldRunOneIntake()) {
            when (RobotState.getMovementDirection()) {
                MechanismSide.SPEAKER -> Intake.setVoltage(0.0, intakingVolts)
                MechanismSide.AMP -> Intake.setVoltage(intakingVolts, 0.0)
            }
        } else {
            Intake.setVoltage(intakingVolts, intakingVolts)
        }

        when {
            RobotState.noteInAmpSideIntakeBeambreak() -> lastSide = MechanismSide.AMP
            RobotState.noteInSpeakerSideIntakeBeambreak() -> lastSide = MechanismSide.SPEAKER
        }
    }

    // End when a note hits the center beambreak
    override fun isFinished(): Boolean {
        return (!RobotState.noteInAmpSideIntakeBeambreak() && lastSide == MechanismSide.AMP) || (!RobotState.noteInSpeakerSideIntakeBeambreak() && lastSide == MechanismSide.SPEAKER)
    }

    override fun end(interrupted: Boolean) {
        // If there is now a note in the intake, check which one and update the robot state accordingly
        lastSide?.let {
            when(it) {
                MechanismSide.AMP -> RobotState.notePosition = NotePosition.AMP_INTAKE
                MechanismSide.SPEAKER -> RobotState.notePosition = NotePosition.SPEAKER_INTAKE
            }
        }

        Intake.stop()
    }
}
