package org.team9432.lib.dashboard.server.sendable

import kotlinx.serialization.Serializable

@Serializable
sealed interface WidgetData: Sendable {
    val name: String
}

@Serializable
data class StringWidget(override val name: String, val value: String, val allowDashboardEdit: Boolean = false): WidgetData

@Serializable
data class BooleanWidget(override val name: String, val value: Boolean, val allowDashboardEdit: Boolean = false): WidgetData

@Serializable
data class DoubleWidget(override val name: String, val value: Double, val allowDashboardEdit: Boolean = false): WidgetData

@Serializable
data class PidWidget(override val name: String, val p: Double, val i: Double, val d: Double, val setpoint: Double, val allowDashboardEdit: Boolean = false): WidgetData