package org.team9432.lib.util

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.geometry.Translation2d
import org.team9432.lib.geometry.Translation2d
import org.team9432.lib.ifBlueElse
import org.team9432.lib.unit.asRotation2d
import org.team9432.lib.unit.degrees
import org.team9432.lib.unit.meters
import org.team9432.robot.FieldConstants


object PoseUtil {
    fun Pose2d.flip() = Pose2d(translation.flip(), rotation.flip())
    fun Translation2d.flip() = Translation2d(FieldConstants.midLine + (FieldConstants.midLine - x.meters), y.meters)
    fun Rotation2d.flip() = Rotation2d.fromDegrees((degrees + 180) * -1)

    val coordinateFlip get() = 1 ifBlueElse -1
    val rotationOffset get() = 0.0.degrees.asRotation2d ifBlueElse 180.0.degrees.asRotation2d

    fun Pose2d.applyFlip() = this ifBlueElse this.flip()
    fun Translation2d.applyFlip() = this ifBlueElse this.flip()
    fun Rotation2d.applyFlip() = this ifBlueElse this.flip()

}