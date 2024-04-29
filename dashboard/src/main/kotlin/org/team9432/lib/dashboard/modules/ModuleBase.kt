package org.team9432.lib.dashboard.modules

import kotlinx.serialization.Serializable

@Serializable
sealed interface Module

@Serializable
sealed interface ModuleBase: Module {
    val name: String
}

@Serializable
sealed interface ModuleGroup: Module {
    val modules: Array<out Module>
}