package org.team9432.robot.subsystems.shooter

import edu.wpi.first.math.geometry.Rotation2d
import org.team9432.lib.commandbased.commands.InstantCommand
import org.team9432.lib.commandbased.commands.SimpleCommand
import org.team9432.robot.subsystems.hood.Hood

/* Interface for interacting with the subsystem through command based systems */
object CommandShooter {
    fun setVoltage(leftVolts: Double, rightVolts: Double) = InstantCommand(Shooter) { Shooter.setVoltage(leftVolts, rightVolts) }
    fun setSpeed(leftRPM: Double, rightRPM: Double) = InstantCommand(Shooter) { Shooter.setSpeed(leftRPM, rightRPM) }
    fun stop() = InstantCommand(Shooter) { Shooter.stop() }
    fun runSpeed(target: () -> Pair<Double, Double>) = SimpleCommand(
        requirements = setOf(Shooter),
        execute = {
            val (left, right) = target.invoke()
            Shooter.setSpeed(left, right)
        },
        end = { Shooter.stop() }
    )
}