package org.team9432.lib.dashboard

import kotlinx.serialization.Serializable

@Serializable
sealed interface Type {
    val name: String
}

@Serializable
data class ImmutableString(override val name: String, val value: String): Type

@Serializable
data class ImmutableBoolean(override val name: String, val value: Boolean): Type

@Serializable
data class MutableBoolean(override val name: String, val value: Boolean): Type

@Serializable
data class ImmutableDouble(override val name: String, val value: Double): Type

@Serializable
data class ImmutablePid(override val name: String, val p: Double, val i: Double, val d: Double, val setpoint: Double): Type