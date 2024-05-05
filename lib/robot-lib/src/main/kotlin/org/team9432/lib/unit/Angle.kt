package org.team9432.lib.unit

import edu.wpi.first.math.geometry.Rotation2d
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

data object Radian: UnitType
typealias Angle = Value<Radian>

const val RADIANS_PER_DEGREE = Math.PI / 180
const val RADIANS_PER_ROTATION = Math.PI * 2

/** Constructs an Angle using this value in radians. */
inline val Double.radians get() = Angle(this)

/** Constructs an Angle using this value in degrees. */
inline val Double.degrees get() = Angle(this * RADIANS_PER_DEGREE)

/** Constructs an Angle using this value in rotations. */
inline val Double.rotations get() = Angle(this * RADIANS_PER_ROTATION)


/** Gets this angle in radians. */
inline val Angle.inRadians get() = value

/** Gets this angle in degrees. */
inline val Angle.inDegrees get() = value / RADIANS_PER_DEGREE

/** Gets this angle in rotations. */
inline val Angle.inRotations get() = value / RADIANS_PER_ROTATION


/** Gets this angle as a [Rotation2d]. */
inline val Angle.asRotation2d get() = Rotation2d(value)


inline val Angle.sin get() = sin(value)
inline val Angle.cos get() = cos(value)
inline val Angle.tan get() = tan(value)