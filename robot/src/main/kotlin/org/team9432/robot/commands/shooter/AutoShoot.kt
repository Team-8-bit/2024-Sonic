package org.team9432.robot.commands.shooter

import org.team9432.lib.commandbased.commands.*
import org.team9432.robot.FieldConstants
import org.team9432.robot.MechanismSide
import org.team9432.robot.RobotState
import org.team9432.robot.commands.drivetrain.TargetAim
import org.team9432.robot.subsystems.Shooter
import org.team9432.robot.subsystems.Superstructure

/** Aims and shoots based on the assumption that the shooter is already running and the note is in the hopper. Only used in auto. */
fun AutoShoot() = SequentialCommand(
    TargetAim { FieldConstants.speakerAimPose },
    WaitUntilCommand { Shooter.atSetpoint() },
    ParallelDeadlineCommand(
        // Shoot the note
        Superstructure.Commands.runLoad(MechanismSide.SPEAKER),

        deadline = WaitCommand(1.0)
    ),

    // Update the note position
    InstantCommand { RobotState.notePosition = RobotState.NotePosition.NONE }
)
