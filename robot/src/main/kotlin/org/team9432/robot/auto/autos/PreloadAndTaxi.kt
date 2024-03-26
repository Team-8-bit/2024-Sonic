package org.team9432.robot.auto.autos

import org.team9432.lib.commandbased.commands.SequentialCommand
import org.team9432.lib.commandbased.commands.SuppliedCommand
import org.team9432.lib.commandbased.commands.WaitCommand
import org.team9432.lib.commandbased.commands.withTimeout
import org.team9432.lib.util.PoseUtil
import org.team9432.robot.auto.AutoBuilder
import org.team9432.robot.auto.commands.AutoShoot
import org.team9432.robot.auto.commands.CollectPreloadAndStartShooter
import org.team9432.robot.commands.drivetrain.DriveFieldRelativeSpeeds
import org.team9432.robot.subsystems.shooter.CommandShooter

fun PreloadAndTaxi() = SequentialCommand(
    SuppliedCommand {
        AutoBuilder.getInitCommand()
    },
    CollectPreloadAndStartShooter(),
    WaitCommand(1.0),
    AutoShoot(),
    WaitCommand(1.0),
    CommandShooter.stop(),

    DriveFieldRelativeSpeeds(0.0, 1.0, 0.0).withTimeout(1.0), // Towards Amp Side
    SuppliedCommand {
        DriveFieldRelativeSpeeds(1.0 * PoseUtil.coordinateFlip, 0.0, 0.0).withTimeout(1.0) // Forwards
    }
)