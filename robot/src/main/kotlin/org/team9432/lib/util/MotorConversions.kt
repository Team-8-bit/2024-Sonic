package org.team9432.lib.util

// Credit to team 364 for the basis of this class
object MotorConversions {
    /**
     * @param rotations NEO rotations
     * @param gearRatio Gear Ratio between NEO and mechanism
     * @return Degrees of rotation of mechanism
     */
    fun NEOToDegrees(rotations: Double, gearRatio: Double): Double {
        return (rotations * 360.0) / gearRatio
    }

    /**
     * @param degrees Degrees of rotation of mechanism
     * @param gearRatio Gear Ratio between NEO and mechanism
     * @return NEO rotations
     */
    fun degreesToNEO(degrees: Double, gearRatio: Double): Double {
        return (degrees / 360.0) * gearRatio
    }

    /**
     * @param rotations NEO rotations
     * @param gearRatio Gear Ratio between NEO and mechanism
     * @return Rotations of mechanism
     */
    fun NEOToRotations(rotations: Double, gearRatio: Double): Double {
        return rotations / gearRatio
    }

    /**
     * @param rotations Rotations of mechanism
     * @param gearRatio Gear Ratio between NEO and mechanism
     * @return NEO rotations
     */
    fun rotationsToNEO(rotations: Double, gearRatio: Double): Double {
        return rotations * gearRatio
    }

    /**
     * @param motorRPM NEO Velocity rotations
     * @param gearRatio Gear Ratio between NEO and mechanism (set to 1 for NEO RPM)
     * @return RPM of mechanism
     */
    fun NEOToRPM(motorRPM: Double, gearRatio: Double): Double {
        return motorRPM / gearRatio
    }

    /**
     * @param RPM       RPM of mechanism
     * @param gearRatio Gear Ratio between NEO and mechanism (set to 1 for NEO RPM)
     * @return RPM of mechanism
     */
    fun RPMToNEO(RPM: Double, gearRatio: Double): Double {
        return RPM * gearRatio
    }

    /**
     * @param motorRPM NEO Velocity rotations
     * @param circumference Circumference of Wheel
     * @param gearRatio Gear Ratio between NEO and mechanism (set to 1 for NEO RPM)
     * @return NEO Velocity rotations
     */
    fun NEOToMPS(motorRPM: Double, circumference: Double, gearRatio: Double): Double {
        val wheelRPM = NEOToRPM(motorRPM, gearRatio)
        return (wheelRPM * circumference) / 60
    }

    /**
     * @param velocity Velocity MPS
     * @param circumference Circumference of Wheel
     * @param gearRatio Gear Ratio between NEO and mechanism (set to 1 for NEO RPM)
     * @return NEO Velocity rotations
     */
    fun MPSToNEO(velocity: Double, circumference: Double, gearRatio: Double): Double {
        val wheelRPM = (velocity * 60) / circumference
        return RPMToNEO(wheelRPM, gearRatio)
    }
}
