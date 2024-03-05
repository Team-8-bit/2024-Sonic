package org.team9432.robot.commands.drivetrain

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.kinematics.ChassisSpeeds
import org.littletonrobotics.junction.Logger
import org.team9432.lib.commandbased.KCommand
import org.team9432.robot.subsystems.RobotPosition
import org.team9432.robot.subsystems.drivetrain.Drivetrain
import org.team9432.robot.subsystems.gyro.Gyro

class StaticSpeakerAlign(private val target: Pose2d): KCommand() {
    override val requirements = setOf(Drivetrain)

    override fun execute() {
        Logger.recordOutput("Drive/AngleTarget", target)

        Drivetrain.setAngleGoal(RobotPosition.angleTo(target))
        val rSpeed = Drivetrain.calculateAngleSpeed()

        val speeds = ChassisSpeeds.fromFieldRelativeSpeeds(0.0, 0.0, rSpeed, Gyro.getYaw())
        Drivetrain.setSpeeds(speeds)
    }

    override fun end(interrupted: Boolean) = Drivetrain.stopAndX()
    override fun isFinished() = Drivetrain.atAngleGoal()
}