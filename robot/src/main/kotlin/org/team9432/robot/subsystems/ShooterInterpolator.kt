package org.team9432.robot.subsystems

import edu.wpi.first.math.interpolation.InterpolatingDoubleTreeMap

object ShooterInterpolator {
    private val map = InterpolatingDoubleTreeMap()

    init {
        map.put(1.2, 10.0)
        map.put(2.0, 25.0)
        map.put(3.0, 30.0)
        map.put(0.0, 0.0)
        map.put(0.0, 0.0)
    }

    fun getHoodAngle(distanceMeters: Double): Double {
        return map.get(distanceMeters)
    }
}