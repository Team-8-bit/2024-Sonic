package org.team9432.lib.dashboard.server.sendable

import kotlinx.serialization.Serializable

/** Represents a request to add a tab to the dashboard. */
@Serializable
data class AddTab(val name: String, val tab: Tab): Sendable

/** Represents a request to remove a tab from the dashboard. */
@Serializable
data class RemoveTab(val name: String): Sendable

/** Represents a tab being sent to the dashboard. */
@Serializable
data class Tab(val name: String, val index: Int, val data: List<TabWidget>)

/** Represents a widget and its size and position on a given tab. */
@Serializable
data class TabWidget(val row: Int, val col: Int, val name: String, val rowsSpanned: Int, val colsSpanned: Int)
