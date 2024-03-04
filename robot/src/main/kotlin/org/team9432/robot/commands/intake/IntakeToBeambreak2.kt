package org.team9432.robot.commands.intake

import org.team9432.lib.commandbased.KCommand
import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.lib.commandbased.commands.SequentialCommand
import org.team9432.lib.commandbased.commands.WaitUntilCommand
import org.team9432.robot.MechanismSide
import org.team9432.robot.RobotState
import org.team9432.robot.RobotState.NotePosition
import org.team9432.robot.subsystems.beambreaks.Beambreaks
import org.team9432.robot.subsystems.intake.CommandIntake
import org.team9432.robot.subsystems.intake.Intake

private var lastSide: MechanismSide? = null

fun IntakeToBeambreak2() = SequentialCommand(
    CommandIntake.runCorrectIntake(-7.0),
    WaitUntilCommand {
        if (Beambreaks.getIntakeAmpSide()) {
            lastSide = MechanismSide.AMP
            return@WaitUntilCommand true
        } else if (Beambreaks.getIntakeSpeakerSide()) {
            lastSide = MechanismSide.SPEAKER
            return@WaitUntilCommand true
        }
        return@WaitUntilCommand false
    },
    CommandIntake.runCorrectIntake(-4.0),
    WaitUntilCommand { !Beambreaks.getIntakeAmpSide() && !Beambreaks.getIntakeSpeakerSide() },
    CommandIntake.stop(),
    InstantCommand {
        when (lastSide) {
            MechanismSide.AMP -> RobotState.notePosition = NotePosition.AMP_INTAKE
            MechanismSide.SPEAKER -> RobotState.notePosition = NotePosition.SPEAKER_INTAKE
            null -> {}
        }
    }
)

//     End when a note hits the center beambreak
//    override fun isFinished(): Boolean {
//        return (!RobotState.noteInAmpSideIntakeBeambreak() && lastSide == MechanismSide.AMP)
//                || (!RobotState.noteInSpeakerSideIntakeBeambreak() && lastSide == MechanismSide.SPEAKER)
//    }
//
//    override fun end(interrupted: Boolean) {
// If there is now a note in the intake, check which one and update the robot state accordingly
//        lastSide?.let {
//            when(it) {
//                MechanismSide.AMP -> RobotState.notePosition = NotePosition.AMP_INTAKE
//                MechanismSide.SPEAKER -> RobotState.notePosition = NotePosition.SPEAKER_INTAKE
//            }
//        }
//
//        Intake.stop()
//    }
//}
