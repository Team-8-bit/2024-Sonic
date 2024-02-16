package org.team9432.lib.util

object RotationUtil {
    /** Converts a rotation (in degrees) to an unsigned value from 0 to 360 degrees */
    fun toUnsignedDegrees(degrees: Double): Double {
        val x = degrees % 360
        return if (x < 0) x + 360 else x
    }

    /** Converts a rotation (in degrees) to a signed value from -180 to 180 degrees */
    fun toSignedDegrees(degrees: Double): Double {
        val x = toUnsignedDegrees(degrees)
        return if (x > 180) x - 360 else x
    }
}