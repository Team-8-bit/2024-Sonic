package org.team9432.lib.dashboard.server.sendable

import kotlinx.serialization.Serializable

@Serializable
data class AddTab(val name: String, val tab: Tab): Sendable

@Serializable
data class Tab(val name: String, val data: List<TabWidget>)

@Serializable
data class TabWidget(val row: Int, val col: Int, val name: String, val rowsSpanned: Int, val colsSpanned: Int)

@Serializable
data class RemoveTab(val name: String): Sendable