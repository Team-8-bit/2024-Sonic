package org.team9432.lib.dashboard

import kotlinx.serialization.Serializable

@Serializable
sealed interface Type

@Serializable
data class ImmutableString(val name: String, val value: String): Type
@Serializable
data class ImmutableBoolean(val name: String, val value: Boolean): Type
@Serializable
data class ImmutableDouble(val name: String, val value: Double): Type

@Serializable
data class ImmutablePid(val name: String, val p: Double, val i: Double, val d: Double, val setpoint: Double): Type