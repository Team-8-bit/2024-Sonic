package org.team9432.robot.subsystems

import edu.wpi.first.math.interpolation.InterpolatingDoubleTreeMap

object ShooterInterpolator {
    private val map = InterpolatingDoubleTreeMap()

    init {
        map.put(0.85, 0.0)
        map.put(1.65, 15.0)
        map.put(2.65, 22.0)
    }

    fun getHoodAngle(distanceMeters: Double): Double {
        return map.get(distanceMeters)
    }
}