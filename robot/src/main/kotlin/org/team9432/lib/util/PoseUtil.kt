package org.team9432.lib.util

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import kotlin.math.atan2


object PoseUtil {
    private const val FIELD_MIDLINE = 16.54175 / 2

    fun flip(input: Pose2d): Pose2d {
        return Pose2d(FIELD_MIDLINE + (FIELD_MIDLINE - input.x), input.y, Rotation2d.fromDegrees((input.rotation.degrees + 180) * -1))
    }
}