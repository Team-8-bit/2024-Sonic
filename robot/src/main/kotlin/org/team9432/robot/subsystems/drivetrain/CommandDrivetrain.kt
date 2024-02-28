package org.team9432.robot.subsystems.drivetrain

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.lib.commandbased.commands.SimpleCommand
import org.team9432.lib.wpilib.ChassisSpeeds
import org.team9432.robot.MechanismSide

/* Interface for interacting with the subsystem through command based systems */
object CommandDrivetrain {
    fun stop() = InstantCommand(Drivetrain) { Drivetrain.stop() }
    fun stopAndX() = InstantCommand(Drivetrain) { Drivetrain.stopAndX() }
}