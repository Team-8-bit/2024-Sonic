package org.team9432.lib.dashboard.modules

import kotlinx.serialization.Serializable

@Serializable
sealed interface ModuleBase {
    val name: String
}