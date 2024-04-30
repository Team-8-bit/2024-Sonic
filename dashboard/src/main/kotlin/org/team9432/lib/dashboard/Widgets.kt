package org.team9432.lib.dashboard

import kotlinx.serialization.Serializable

@Serializable
sealed interface Widget {
    val name: String
}

@Serializable
data class ImmutableString(override val name: String, val value: String): Widget

@Serializable
data class ImmutableBoolean(override val name: String, val value: Boolean): Widget

@Serializable
data class MutableBoolean(override val name: String, val value: Boolean): Widget

@Serializable
data class ImmutableDouble(override val name: String, val value: Double): Widget

@Serializable
data class ImmutablePid(override val name: String, val p: Double, val i: Double, val d: Double, val setpoint: Double): Widget