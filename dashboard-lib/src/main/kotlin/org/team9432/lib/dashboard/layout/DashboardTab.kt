package org.team9432.lib.dashboard.layout

import org.team9432.lib.dashboard.server.sendable.Tab
import org.team9432.lib.dashboard.server.sendable.TabWidget

class DashboardTab(rows: Int, cols: Int, val name: String = "Unnamed") {
    private data class Coordinate(val row: Int, val col: Int)

    private val rowIndices = 0..<rows
    private val colIndices = 0..<cols

    private val usedCoordinates = mutableMapOf<Coordinate, String>()
    private val registeredWidgets = mutableListOf<TabWidget>()

    fun addWidget(row: Int, col: Int, name: String, rowsSpanned: Int = 1, colsSpanned: Int = 1) {
        require(row + (rowsSpanned - 1) in rowIndices && col + (colsSpanned - 1) in colIndices) { "This widget does not fit within the grid!" }

        val rowsUsed = row..<(row + rowsSpanned)
        val colsUsed = col..<(col + colsSpanned)

        val coordinatesUsed = mutableListOf<Coordinate>()
        rowsUsed.forEach { rowIndex ->
            colsUsed.forEach { colIndex ->
                val coordinate = Coordinate(rowIndex, colIndex)
                coordinatesUsed.add(coordinate)

                val value = usedCoordinates[coordinate]
                if (value != null) {
                    throw Exception("The coordinate (row=$rowIndex, col=$colIndex) is already occupied by $value!")
                }
            }
        }

        coordinatesUsed.forEach {
            usedCoordinates[it] = name
        }

        registeredWidgets.add(TabWidget(row, col, name, rowsSpanned, colsSpanned))
    }

    fun getSendable(): Tab = Tab(name, registeredWidgets)
}