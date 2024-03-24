package org.team9432.robot.commands.hood

import edu.wpi.first.math.geometry.Rotation2d
import org.littletonrobotics.junction.Logger
import org.team9432.robot.subsystems.RobotPosition
import org.team9432.robot.subsystems.ShooterInterpolator
import org.team9432.robot.subsystems.hood.CommandHood

fun HoodAimAtSpeaker() = CommandHood.followAngle {
    Rotation2d.fromDegrees(ShooterInterpolator.getHoodAngle(RobotPosition.distanceToSpeaker())).also {
        Logger.recordOutput("Hood/ShootTarget", it.degrees)
    }
}