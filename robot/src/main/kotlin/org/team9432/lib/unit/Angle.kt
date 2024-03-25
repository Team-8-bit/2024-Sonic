package org.team9432.lib.unit

import edu.wpi.first.math.geometry.Rotation2d
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

data object Radian: UnitType

typealias Angle = Value<Radian>

const val RADIANS_PER_DEGREE = Math.PI / 180
const val RADIANS_PER_ROTATION = Math.PI * 2

inline val Double.radians: Angle
    get() = Angle(this)

inline val Double.degrees: Angle
    get() = Angle(this * RADIANS_PER_DEGREE)

inline val Double.rotations: Angle
    get() = Angle(this * RADIANS_PER_ROTATION)

inline val Angle.inRadians: Double
    get() = value

inline val Angle.inDegrees: Double
    get() = value / RADIANS_PER_DEGREE

inline val Angle.inRotations: Double
    get() = value / RADIANS_PER_ROTATION

inline val Angle.asRotation2d: Rotation2d
    get() = Rotation2d(value)

inline val Angle.sin: Double
    get() = sin(value)

inline val Angle.cos: Double
    get() = cos(value)

inline val Angle.tan: Double
    get() = tan(value)