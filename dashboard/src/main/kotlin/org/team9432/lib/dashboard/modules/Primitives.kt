package org.team9432.lib.dashboard.modules

import kotlinx.serialization.Serializable

@Serializable
data class StringModule(override val name: String): ModuleBase

@Serializable
data class BooleanModule(override val name: String): ModuleBase

@Serializable
data class DoubleModule(override val name: String): ModuleBase