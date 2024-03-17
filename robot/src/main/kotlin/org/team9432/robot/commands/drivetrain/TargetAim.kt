package org.team9432.robot.commands.drivetrain

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.kinematics.ChassisSpeeds
import org.littletonrobotics.junction.Logger
import org.team9432.Robot.applyFlip
import org.team9432.lib.commandbased.KCommand
import org.team9432.robot.MechanismSide
import org.team9432.robot.subsystems.RobotPosition
import org.team9432.robot.subsystems.drivetrain.Drivetrain
import org.team9432.robot.sensors.gyro.Gyro

class TargetAim(
    private val side: MechanismSide = MechanismSide.SPEAKER,
    private val target: () -> Pose2d,
): KCommand() {
    override val requirements = setOf(Drivetrain)

    override fun initialize() {
        Drivetrain.apriltagStrategy = Drivetrain.ApriltagStrategy.WHILE_NOT_MOVING
    }

    override fun execute() {
        val currentTarget = target.invoke().applyFlip()
        Logger.recordOutput("Drive/AngleTarget", currentTarget)

        when (side) {
            MechanismSide.SPEAKER -> Drivetrain.setAngleGoal(RobotPosition.angleTo(currentTarget))
            MechanismSide.AMP -> Drivetrain.setAngleGoal(RobotPosition.angleTo(currentTarget).plus(Rotation2d(Math.PI)))
        }

        val rSpeed = Drivetrain.calculateAngleSpeed()

        val speeds = ChassisSpeeds.fromFieldRelativeSpeeds(0.0, 0.0, rSpeed, Gyro.getYaw())
        Drivetrain.setSpeeds(speeds)
    }

    override fun isFinished() = Drivetrain.atAngleGoal()
    override fun end(interrupted: Boolean) {
        Drivetrain.stop()
        Drivetrain.apriltagStrategy = Drivetrain.ApriltagStrategy.ALWAYS
    }
}