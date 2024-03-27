package org.team9432.robot.commands.hood

import edu.wpi.first.math.geometry.Rotation2d
import org.littletonrobotics.junction.Logger
import org.team9432.robot.RobotPosition
import org.team9432.robot.ShooterInterpolator
import org.team9432.robot.subsystems.Hood

fun HoodAimAtSpeaker() = Hood.Commands.followAngle {
    Rotation2d.fromDegrees(ShooterInterpolator.getHoodAngle(RobotPosition.distanceToSpeaker())).also {
        Logger.recordOutput("Hood/ShootTarget", it.degrees)
    }
}