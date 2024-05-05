package org.team9432.lib.constants

import edu.wpi.first.math.geometry.Pose2d
import org.team9432.lib.unit.feet
import org.team9432.lib.unit.inMeters
import org.team9432.lib.unit.inches

/**
 * - Robot rotation is always 0 degrees when the front of the robot is facing the red alliance wall
 * - (0,0) coordinate on the far right of the blue driver station wall (as though you were standing behind it)
 * - +x is towards the red alliance wall
 * - +y is towards the left side of the field (again standing behind the blue driver station)
 */
object EvergreenFieldConstants {
    val lengthY = 26.0.feet + 11.25.inches
    val lengthX = 54.0.feet + 3.25.inches
    val centerY = lengthY / 2
    val centerX = lengthX / 2

    fun Pose2d.isOnField() = (x >= 0 && x <= lengthX.inMeters) && (y >= 0 && y <= lengthY.inMeters)
}