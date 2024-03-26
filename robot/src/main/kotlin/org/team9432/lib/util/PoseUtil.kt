package org.team9432.lib.util

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.geometry.Translation2d
import org.team9432.lib.geometry.Pose2d
import org.team9432.lib.geometry.Translation2d
import org.team9432.lib.unit.meters
import org.team9432.robot.FieldConstants


object PoseUtil {
    fun flip(input: Pose2d): Pose2d {
        return Pose2d(FieldConstants.midLine + (FieldConstants.midLine - input.x.meters), input.y.meters, Rotation2d.fromDegrees((input.rotation.degrees + 180) * -1))
    }

    fun flip(input: Translation2d): Translation2d {
        return Translation2d(FieldConstants.midLine + (FieldConstants.midLine - input.x.meters), input.y.meters)
    }

    fun flip(input: Rotation2d): Rotation2d {
        return Rotation2d.fromDegrees((input.degrees + 180) * -1)
    }
}