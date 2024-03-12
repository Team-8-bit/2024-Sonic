package org.team9432.robot.commands.drivetrain

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.kinematics.ChassisSpeeds
import org.littletonrobotics.junction.Logger
import org.team9432.lib.commandbased.KCommand
import org.team9432.robot.commands.CommandConstants.SHOOT_ON_MOVE_SECS
import org.team9432.robot.subsystems.RobotPosition
import org.team9432.robot.subsystems.drivetrain.Drivetrain
import org.team9432.robot.subsystems.gyro.Gyro

class TargetDriveSpeeds(private val vx: Double = 0.0, private val vy: Double = 0.0, private val target: () -> Pose2d): KCommand() {
    override val requirements = setOf(Drivetrain)

    override fun execute() {
        val currentTarget = target.invoke()
        Logger.recordOutput("Drive/AngleTarget", currentTarget)

        Drivetrain.setAngleGoal(RobotPosition.angleTo(currentTarget, SHOOT_ON_MOVE_SECS))
        val vr = Drivetrain.calculateAngleSpeed()

        Drivetrain.setSpeeds(ChassisSpeeds.fromFieldRelativeSpeeds(vx, vy, vr, Gyro.getYaw()))
    }

    override fun end(interrupted: Boolean) {
        Drivetrain.stop()
    }
}