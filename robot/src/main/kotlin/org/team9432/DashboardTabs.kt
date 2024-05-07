package org.team9432

import org.team9432.dashboard.lib.Dashboard
import org.team9432.dashboard.lib.layout.DashboardTab
import org.team9432.dashboard.shared.WidgetType

object DashboardTabs {
    private val testing = DashboardTab(rows = 6, cols = 10, 0, "Testing")
    private val test2 = DashboardTab(rows = 6, cols = 10, 1, "TestingTwo")

    init {
        testing.addWidget(row = 0, col = 0, "Teleop", WidgetType.DisplayOnlyBoolean)
        testing.addWidget(row = 1, col = 0, "Disabled", WidgetType.DisplayOnlyBoolean)
        testing.addWidget(row = 2, col = 0, "Autonomous", WidgetType.DisplayOnlyBoolean)
        testing.addWidget(row = 3, col = 3, "count", WidgetType.DisplayOnlyDouble, rowsSpanned = 2, colsSpanned = 2)
        testing.addWidget(row = 1, col = 1, "Alliance", WidgetType.DisplayOnlyString, colsSpanned = 2)
        testing.addWidget(row = 0, col = 1, "LEDTest", WidgetType.WritableBoolean, colsSpanned = 2)

        test2.addWidget(row = 5, col = 0, "Teleop", WidgetType.DisplayOnlyBoolean)
        test2.addWidget(row = 1, col = 0, "Disabled", WidgetType.DisplayOnlyBoolean)
        test2.addWidget(row = 4, col = 0, "Autonomous", WidgetType.DisplayOnlyBoolean)
        test2.addWidget(row = 2, col = 3, "count", WidgetType.DisplayOnlyDouble, rowsSpanned = 2, colsSpanned = 2)
        test2.addWidget(row = 1, col = 4, "Alliance", WidgetType.DisplayOnlyString, colsSpanned = 2)
        test2.addWidget(row = 4, col = 2, "LEDTest", WidgetType.WritableBoolean, colsSpanned = 2)
    }

    fun sendToDashboard() {
        Dashboard.addTab(testing.getSendable())
        Dashboard.addTab(test2.getSendable())
    }
}