package org.team9432.lib.dashboard.modules

import kotlinx.serialization.Serializable

@Serializable
class Row(override vararg val modules: Module): ModuleGroup

@Serializable
class Col(override vararg val modules: Module): ModuleGroup