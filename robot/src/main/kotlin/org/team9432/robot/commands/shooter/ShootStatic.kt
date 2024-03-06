package org.team9432.robot.commands.shooter

import org.team9432.lib.commandbased.commands.*
import org.team9432.robot.MechanismSide
import org.team9432.robot.RobotState
import org.team9432.robot.commands.hopper.MoveToSide
import org.team9432.robot.subsystems.hopper.CommandHopper
import org.team9432.robot.subsystems.intake.CommandIntake
import org.team9432.robot.subsystems.shooter.CommandShooter

fun ShootStatic(rpmLeft: Double, rpmRight: Double) = SuppliedCommand {
    // Don't run this if there's not a note in the intake
    if (!RobotState.notePosition.isIntake) InstantCommand {}
    else {
        SequentialCommand(
            ParallelCommand(
                // Spin up the shooter
                CommandShooter.setSpeed(rpmLeft, rpmRight),
                // Move the note to the speaker side of the hopper
                MoveToSide(MechanismSide.SPEAKER),
                // Minimum of one second to spin up the shooter
                WaitCommand(1.0),
            ),
            // Shoot the note
            CommandHopper.loadTo(MechanismSide.SPEAKER, 5.0),
            CommandIntake.intakeSide(MechanismSide.SPEAKER, 5.0),
            // Wait a second, then stop the motors
            WaitCommand(1.0),
            CommandShooter.stop(),
            CommandHopper.stop(),
            CommandIntake.stop(),
            // Update the note position
            InstantCommand { RobotState.notePosition = RobotState.NotePosition.NONE }
        )
    }
}
