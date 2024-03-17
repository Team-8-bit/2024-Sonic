package org.team9432.robot.commands.drivetrain

import edu.wpi.first.math.kinematics.ChassisSpeeds
import org.team9432.lib.commandbased.KCommand
import org.team9432.robot.subsystems.drivetrain.Drivetrain

class DriveRobotRelativeSpeeds(private val vx: Double = 0.0, private val vy: Double = 0.0, private val vr: Double = 0.0): KCommand() {
    override val requirements = setOf(Drivetrain)

    override fun execute() {
        Drivetrain.setSpeeds(ChassisSpeeds(vx, vy, vr))
    }

    override fun end(interrupted: Boolean) {
        Drivetrain.stop()
    }
}