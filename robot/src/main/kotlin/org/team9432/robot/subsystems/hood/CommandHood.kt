package org.team9432.robot.subsystems.hood

import edu.wpi.first.math.geometry.Rotation2d
import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.robot.MechanismSide

/* Interface for interacting with the subsystem through command based systems */
object CommandHood {
    fun stop() = InstantCommand(Hood) { Hood.stop() }
    fun setAngle(angle: Rotation2d) = InstantCommand(Hood) { Hood.setAngle(angle) }
}