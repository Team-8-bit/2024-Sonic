package org.team9432.lib.dashboard.modules

import kotlinx.serialization.Serializable

@Serializable
data class TextModule(override val name: String, var value: String): ModuleBase

@Serializable
data class BooleanModule(override val name: String, var value: Boolean): ModuleBase

@Serializable
data class DoubleModule(override val name: String, var value: Double): ModuleBase