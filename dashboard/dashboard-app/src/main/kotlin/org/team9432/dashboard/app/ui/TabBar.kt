package org.team9432.dashboard.app.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.team9432.dashboard.app.io.Client

/** Displays the list of tabs provided by the robot code. */
@Composable
fun TabBar() {
    Row {
        TabRow(AppState.currentTab, modifier = Modifier) {
            Client.currentTabs.values.forEach {
                Tab(it.index == AppState.currentTab,
                    onClick = { AppState.currentTab = it.index },
                    text = { Text(it.name) }
                )
            }
        }
    }
}