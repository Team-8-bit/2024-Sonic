package org.team9432

import org.team9432.lib.dashboard.Dashboard
import org.team9432.lib.dashboard.layout.DashboardTab

object DashboardTabs {
    private val testing = DashboardTab(rows = 6, cols = 10, "Testing")

    init {
        testing.addWidget(row = 0, col = 0, "Teleop")
        testing.addWidget(row = 1, col = 0, "Disabled")
        testing.addWidget(row = 2, col = 0, "Autonomous")
        testing.addWidget(row = 3, col = 3, "count", rowsSpanned = 2, colsSpanned = 2)
        testing.addWidget(row = 1, col = 1, "Alliance", colsSpanned = 2)
        testing.addWidget(row = 0, col = 1, "LEDTest", colsSpanned = 2)
    }

    fun sendToDashboard() {
        Dashboard.addTab(testing.getSendable())
    }
}