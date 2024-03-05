package org.team9432.robot.commands.drivetrain

import edu.wpi.first.math.geometry.Pose2d
import org.team9432.lib.commandbased.KCommand
import org.team9432.lib.wpilib.ChassisSpeeds
import org.team9432.robot.subsystems.drivetrain.Drivetrain
import org.team9432.robot.subsystems.gyro.Gyro

class DriveToPosition(private val position: Pose2d): KCommand() {
    override val requirements = setOf(Drivetrain)

    override fun initialize() {
        Drivetrain.setPositionGoal(position)
    }

    override fun execute() {
        Drivetrain.setSpeeds(ChassisSpeeds.toFieldRelativeSpeeds(Drivetrain.calculatePositionSpeed(), Gyro.getYaw()))
    }

    override fun isFinished(): Boolean {
        return Drivetrain.atPositionGoal()
    }
}

