package org.team9432.lib.unit

import kotlin.math.pow
import kotlin.math.sqrt

sealed interface UnitType

@JvmInline
value class Value<T: UnitType>(val value: Double) {
    operator fun plus(other: Value<T>): Value<T> = Value(value + other.value)
    operator fun minus(other: Value<T>): Value<T> = Value(value - other.value)
    operator fun times(other: Value<T>): Value<T> = Value(value * other.value)
    operator fun times(num: Double): Value<T> = Value(value * num)
    operator fun times(num: Number): Value<T> = Value(value * num.toDouble())
    operator fun div(other: Value<T>): Value<T> = Value(value / other.value)
    operator fun div(num: Double): Value<T> = Value(value / num)
    operator fun div(num: Number): Value<T> = Value(value / num.toDouble())

    fun pow(exp: Int): Value<T> = Value(value.pow(exp))
    fun sqrt(): Value<T> = Value(sqrt(value))

    operator fun unaryMinus(): Value<T> = Value(-value)

    operator fun compareTo(other: Value<T>): Int = value.compareTo(other.value)
}

operator fun Double.compareTo(other: Value<*>): Int = compareTo(other.value)
operator fun Value<*>.compareTo(other: Double): Int = value.compareTo(other)
