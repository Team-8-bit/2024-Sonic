package org.team9432.robot.subsystems

import edu.wpi.first.math.interpolation.InterpolatingDoubleTreeMap

object ShooterInterpolator {
    private val map = InterpolatingDoubleTreeMap()

    init {
        map.put(1.0, 0.0)
        map.put(1.8, 15.0)
        map.put(2.8, 22.0)
    }

    fun getHoodAngle(distanceMeters: Double): Double {
        return map.get(distanceMeters)
    }
}