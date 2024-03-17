package org.team9432.robot.commands

import org.team9432.lib.commandbased.KCommandScheduler
import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.robot.RobotState
import org.team9432.robot.subsystems.amp.Amp
import org.team9432.robot.subsystems.climber.LeftClimber
import org.team9432.robot.subsystems.climber.RightClimber
import org.team9432.robot.subsystems.hood.Hood
import org.team9432.robot.subsystems.hopper.Hopper
import org.team9432.robot.subsystems.intake.Intake
import org.team9432.robot.subsystems.shooter.Shooter

fun stop() {
    RobotState.notePosition = RobotState.NotePosition.NONE
    KCommandScheduler.cancelAll()
    Intake.stop()
    Hopper.stop()
    Shooter.stop()
    Amp.stop()
    LeftClimber.stop()
    RightClimber.stop()
    Hood.stop()
}

fun stopCommand() = InstantCommand { stop() }