package org.team9432

import org.team9432.dashboard.lib.Dashboard
import org.team9432.dashboard.lib.layout.DashboardTab

object DashboardTabs {
    private val testing = DashboardTab(rows = 6, cols = 10, 0, "Testing")
    private val test2 = DashboardTab(rows = 6, cols = 10, 1, "TestingTwo")

    init {
        testing.addWidget(row = 0, col = 0, "Teleop")
        testing.addWidget(row = 1, col = 0, "Disabled")
        testing.addWidget(row = 2, col = 0, "Autonomous")
        testing.addWidget(row = 3, col = 3, "count", rowsSpanned = 2, colsSpanned = 2)
        testing.addWidget(row = 1, col = 1, "Alliance", colsSpanned = 2)
        testing.addWidget(row = 0, col = 1, "LEDTest", colsSpanned = 2)

        test2.addWidget(row = 5, col = 0, "Teleop")
        test2.addWidget(row = 1, col = 0, "Disabled")
        test2.addWidget(row = 4, col = 0, "Autonomous")
        test2.addWidget(row = 2, col = 3, "count", rowsSpanned = 2, colsSpanned = 2)
        test2.addWidget(row = 1, col = 4, "Alliance", colsSpanned = 2)
        test2.addWidget(row = 4, col = 2, "LEDTest", colsSpanned = 2)
    }

    fun sendToDashboard() {
        Dashboard.addTab(testing.getSendable())
        Dashboard.addTab(test2.getSendable())
    }
}