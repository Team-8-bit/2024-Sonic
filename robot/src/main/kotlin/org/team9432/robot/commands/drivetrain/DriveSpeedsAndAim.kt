package org.team9432.robot.commands.drivetrain

import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.kinematics.ChassisSpeeds
import org.littletonrobotics.junction.Logger
import org.team9432.lib.commandbased.KCommand
import org.team9432.robot.Controls
import org.team9432.robot.subsystems.drivetrain.Drivetrain
import org.team9432.robot.subsystems.gyro.Gyro

class DriveSpeedsAndAim(
    private val angle: () -> Rotation2d,
    private val vx: Double = 0.0,
    private val vy: Double = 0.0
) : KCommand() {
    override val requirements = setOf(Drivetrain)

    override fun execute() {
        val currentTarget = angle.invoke()
        Logger.recordOutput("Drive/TargetAngle", currentTarget)

        Drivetrain.setAngleGoal(currentTarget)
        val vr = Drivetrain.calculateAngleSpeed()

        val speeds = ChassisSpeeds.fromFieldRelativeSpeeds(vx, vy, vr, Gyro.getYaw())

        Drivetrain.setSpeeds(speeds)
    }
}