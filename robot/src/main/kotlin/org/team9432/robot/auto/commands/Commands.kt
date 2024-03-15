package org.team9432.robot.auto.commands

import edu.wpi.first.math.geometry.Rotation2d
import org.team9432.Robot
import org.team9432.lib.commandbased.KCommand
import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.lib.commandbased.commands.ParallelCommand
import org.team9432.lib.commandbased.commands.SequentialCommand
import org.team9432.lib.commandbased.commands.WaitCommand
import org.team9432.robot.MechanismSide
import org.team9432.robot.RobotState
import org.team9432.robot.commands.hopper.MoveToSide
import org.team9432.robot.commands.intake.FinishIntakingAndAlign
import org.team9432.robot.subsystems.gyro.Gyro
import org.team9432.robot.subsystems.shooter.CommandShooter
import org.team9432.robot.subsystems.shooter.Shooter

fun FinishIntakingThen(command: KCommand, delay: Double = 0.5) = ParallelCommand(
    SequentialCommand(
        WaitCommand(delay),
        command
    ),
    FinishIntakingAndLoadToSpeaker()
)

fun FinishIntakingAndLoadToSpeaker() = SequentialCommand(
    FinishIntakingAndAlign(),
    MoveToSide(MechanismSide.SPEAKER)
)

fun InitAuto(degrees: Rotation2d) = InstantCommand {
    Shooter.stop()
    RobotState.notePosition = RobotState.NotePosition.SPEAKER_HOPPER
    Gyro.setYaw(degrees.plus(Robot.rotationOffset))
}

fun ExitAuto() = ParallelCommand(
    CommandShooter.stop()
)