package org.team9432

import org.team9432.dashboard.lib.Dashboard

object DashboardTabs {
    fun sendToDashboard() {
        Dashboard.addTab("Testing", 0, rows = 6, cols = 10)
    }
}