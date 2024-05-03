package org.team9432.lib.dashboard.server.sendable

import kotlinx.serialization.Serializable

/** The base of select primitive widgets. */

@Serializable
sealed interface WidgetData: Sendable {
    val name: String
}

/** Represents a simple string widget being sent to or from the dashboard. */
@Serializable
data class StringWidget(override val name: String, val value: String, val allowDashboardEdit: Boolean = false): WidgetData

/** Represents a simple boolean widget being sent to or from the dashboard. */
@Serializable
data class BooleanWidget(override val name: String, val value: Boolean, val allowDashboardEdit: Boolean = false): WidgetData

/** Represents a simple double widget being sent to or from the dashboard. */
@Serializable
data class DoubleWidget(override val name: String, val value: Double, val allowDashboardEdit: Boolean = false): WidgetData