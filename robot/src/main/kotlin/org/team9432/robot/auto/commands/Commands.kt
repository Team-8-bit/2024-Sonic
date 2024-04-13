package org.team9432.robot.auto.commands

import edu.wpi.first.math.geometry.Rotation2d
import org.team9432.lib.commandbased.KCommand
import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.lib.commandbased.commands.ParallelCommand
import org.team9432.lib.commandbased.commands.SequentialCommand
import org.team9432.lib.commandbased.commands.WaitCommand
import org.team9432.lib.util.PoseUtil
import org.team9432.robot.RobotState
import org.team9432.robot.RobotState.NotePosition
import org.team9432.robot.commands.intake.FinishIntakingAndAlign
import org.team9432.robot.sensors.gyro.Gyro
import org.team9432.robot.subsystems.Shooter

fun FinishIntakingThen(command: KCommand, delay: Double = 0.25) = ParallelCommand(
    SequentialCommand(
        WaitCommand(delay),
        command
    ),
    FinishIntakingAndLoadToSpeaker()
)

fun FinishIntakingAndLoadToSpeaker() = FinishIntakingAndAlign(positionToLoadTo = NotePosition.SPEAKER_HOPPER)

fun InitAuto(degrees: Rotation2d) = InstantCommand {
    Shooter.stop()
    RobotState.notePosition = NotePosition.SPEAKER_HOPPER
    Gyro.setYaw(degrees.plus(PoseUtil.rotationOffset))
}