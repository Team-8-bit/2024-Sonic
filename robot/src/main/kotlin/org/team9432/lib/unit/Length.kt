package org.team9432.lib.unit

data object Meter: UnitType

typealias Length = Value<Meter>

const val METERS_PER_INCH = 0.0254
const val METERS_PER_FOOT = METERS_PER_INCH * 12

inline val Double.meters: Length
    get() = Length(this)

inline val Double.inches: Length
    get() = Length(this * METERS_PER_INCH)

inline val Double.feet: Length
    get() = Length(this * METERS_PER_FOOT)

inline val Length.inMeters: Double
    get() = value

inline val Length.inInches: Double
    get() = value / METERS_PER_INCH

inline val Length.inFeet: Double
    get() = value / METERS_PER_FOOT
