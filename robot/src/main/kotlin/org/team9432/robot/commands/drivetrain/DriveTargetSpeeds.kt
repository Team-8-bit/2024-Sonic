package org.team9432.robot.commands.drivetrain

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.kinematics.ChassisSpeeds
import org.littletonrobotics.junction.Logger
import org.team9432.lib.commandbased.KCommand
import org.team9432.robot.Controls
import org.team9432.robot.subsystems.RobotPosition
import org.team9432.robot.subsystems.drivetrain.Drivetrain
import org.team9432.robot.subsystems.gyro.Gyro

class DriveTargetSpeeds(
    private val target: () -> Pose2d,
    private val vx: Double = 0.0,
    private val vy: Double = 0.0,
    private val fieldOriented: Boolean = true,
) : KCommand() {
    override val requirements = setOf(Drivetrain)

    override fun execute() {
        val currentTarget = target.invoke()
        Logger.recordOutput("Drive/AngleTarget", currentTarget)

        Drivetrain.setAngleGoal(RobotPosition.angleTo(currentTarget))
        val vr = Drivetrain.calculateAngleSpeed()

        val speeds = if (fieldOriented) {
            ChassisSpeeds.fromFieldRelativeSpeeds(vx, vy, vr, Gyro.getYaw())
        } else {
            ChassisSpeeds(vx, vy, vr)
        }

        Drivetrain.setSpeeds(speeds)
    }
}