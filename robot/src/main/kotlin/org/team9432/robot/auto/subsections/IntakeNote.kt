package org.team9432.robot.auto.subsections

import edu.wpi.first.math.geometry.Pose2d
import org.team9432.lib.commandbased.commands.*
import org.team9432.robot.MechanismSide
import org.team9432.robot.RobotState
import org.team9432.robot.auto.AllianceNote
import org.team9432.robot.auto.AutoConstants
import org.team9432.robot.commands.CommandConstants
import org.team9432.robot.commands.drivetrain.DriveRobotRelativeSpeeds
import org.team9432.robot.commands.drivetrain.DriveToPosition
import org.team9432.robot.commands.drivetrain.TargetAim
import org.team9432.robot.sensors.beambreaks.BeambreakIOSim
import org.team9432.robot.subsystems.intake.CommandIntake

fun IntakeNote(note: AllianceNote) = SequentialCommand(
    // Move forwards until the note is touched
    ParallelDeadlineCommand(
        CommandIntake.runIntakeSide(MechanismSide.AMP, CommandConstants.INITIAL_INTAKE_VOLTS),

        SequentialCommand(
            TargetAim(MechanismSide.AMP) { AutoConstants.getNotePosition(note) },
            ParallelDeadlineCommand(
                DriveRobotRelativeSpeeds(vx = -1.0),
                deadline = WaitCommand(1.0)
            )
        ),

        deadline = WaitUntilCommand { RobotState.noteInAmpSideIntakeBeambreak() }.afterSimDelay(1.0) { BeambreakIOSim.setNoteInIntakeAmpSide(true) }.withTimeout(2.0)
    )
)

fun IntakeNote(pose: Pose2d, timeout: Double = 1.0) = SequentialCommand(
    // Move forwards until the note is touched
    ParallelDeadlineCommand(
        CommandIntake.runIntakeSide(MechanismSide.AMP, CommandConstants.INITIAL_INTAKE_VOLTS),

        SequentialCommand(
            DriveToPosition(pose),
            ParallelDeadlineCommand(
                DriveRobotRelativeSpeeds(vx = -1.0),
                deadline = WaitCommand(timeout)
            ).afterSimDelay(0.25) { BeambreakIOSim.setNoteInIntakeAmpSide(true) }
        ),
        deadline = WaitUntilCommand { RobotState.noteInAmpSideIntakeBeambreak() }
    )
)