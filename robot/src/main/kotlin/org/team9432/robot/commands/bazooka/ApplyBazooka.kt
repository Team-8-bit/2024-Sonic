package org.team9432.robot.commands.bazooka

import org.team9432.lib.commandbased.commands.SequentialCommand
import org.team9432.lib.commandbased.commands.SuppliedCommand
import org.team9432.lib.commandbased.commands.WaitCommand
import org.team9432.robot.FieldConstants
import org.team9432.robot.commands.amp.ScoreAmp
import org.team9432.robot.commands.drivetrain.DriveToPosition
import org.team9432.robot.commands.shooter.TrapNote
import org.team9432.robot.subsystems.Bazooka

fun ApplyBazooka() = SequentialCommand(
//    Bazooka.Commands.setVoltage(12.0),
//    WaitCommand(0.5),
    SuppliedCommand { DriveToPosition(FieldConstants.getTrapAimPosition()) },
    TrapNote(),
    WaitCommand(1.0),
//    Bazooka.Commands.stop()
)
