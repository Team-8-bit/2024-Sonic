package org.team9432.lib.unit

data object Second: UnitType

typealias Time = Value<Second>

const val SECONDS_PER_MILLISECOND = 0.001
const val SECONDS_PER_NANOSECOND = 1e+9

inline val Double.seconds: Time
    get() = Time(this)

inline val Double.milliseconds: Time
    get() = Time(this * SECONDS_PER_MILLISECOND)

inline val Double.nanoseconds: Time
    get() = Time(this * SECONDS_PER_NANOSECOND)

inline val Int.seconds: Time
    get() = Time(this.toDouble())

inline val Int.milliseconds: Time
    get() = Time(this * SECONDS_PER_MILLISECOND)

inline val Int.nanoseconds: Time
    get() = Time(this * SECONDS_PER_NANOSECOND)

inline val Time.inSeconds: Double
    get() = value

inline val Time.inMilliseconds: Double
    get() = value / SECONDS_PER_MILLISECOND

inline val Time.inNanoseconds: Long
    get() = (value / SECONDS_PER_NANOSECOND).toLong()
