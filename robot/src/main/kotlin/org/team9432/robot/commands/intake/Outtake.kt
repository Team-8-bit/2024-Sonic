package org.team9432.robot.commands.intake

import org.team9432.lib.commandbased.commands.*
import org.team9432.robot.MechanismSide
import org.team9432.robot.RobotState
import org.team9432.robot.subsystems.beambreaks.BeambreakIOSim
import org.team9432.robot.subsystems.hopper.CommandHopper
import org.team9432.robot.subsystems.intake.CommandIntake
import org.team9432.robot.subsystems.intake.Intake

fun Outtake() = SimpleCommand(
    requirements = setOf(Intake),
    initialize = { Intake.setVoltage(-8.0, -8.0) },
    end = { Intake.stop() }
)