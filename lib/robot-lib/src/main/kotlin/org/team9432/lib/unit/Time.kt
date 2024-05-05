package org.team9432.lib.unit

data object Second: UnitType
typealias Time = Value<Second>

const val SECONDS_PER_MILLISECOND = 0.001

/** Constructs a time using this value in seconds. */
inline val Double.seconds get() = Time(this)

/** Constructs a time using this value in milliseconds. */
inline val Double.milliseconds get() = Time(this * SECONDS_PER_MILLISECOND)


/** Constructs a time using this value in seconds. */
inline val Int.seconds get() = Time(this.toDouble())

/** Constructs a time using this value in milliseconds. */
inline val Int.milliseconds get() = Time(this * SECONDS_PER_MILLISECOND)


/** Gets this time in seconds. */
inline val Time.inSeconds get() = value

/** Gets this time in milliseconds. */
inline val Time.inMilliseconds get() = value / SECONDS_PER_MILLISECOND