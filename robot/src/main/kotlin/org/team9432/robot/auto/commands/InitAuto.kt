package org.team9432.robot.auto.commands

import edu.wpi.first.math.geometry.Rotation2d
import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.lib.commandbased.commands.SequentialCommand
import org.team9432.robot.RobotState
import org.team9432.robot.subsystems.gyro.Gyro
import org.team9432.robot.subsystems.shooter.CommandShooter

fun InitAuto(degrees: Rotation2d) = SequentialCommand(
    InstantCommand {
        RobotState.notePosition = RobotState.NotePosition.SPEAKER_HOPPER
        Gyro.setYaw(degrees)
    },
    PullFromSpeakerShooter(),
    CommandShooter.setSpeed(4000.0, 6000.0)
)