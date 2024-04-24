package org.team9432.lib.unit

data object Meter: UnitType
typealias Length = Value<Meter>

const val METERS_PER_INCH = 0.0254
const val METERS_PER_FOOT = METERS_PER_INCH * 12

/** Constructs a length using this value in meters. */
inline val Double.meters get() = Length(this)

/** Constructs a length using this value in inches. */
inline val Double.inches get() = Length(this * METERS_PER_INCH)

/** Constructs a length using this value in feet. */
inline val Double.feet get() = Length(this * METERS_PER_FOOT)


/** Gets this length in meters. */
inline val Length.inMeters get() = value

/** Gets this length in inches. */
inline val Length.inInches get() = value / METERS_PER_INCH

/** Gets this length in feet. */
inline val Length.inFeet get() = value / METERS_PER_FOOT
