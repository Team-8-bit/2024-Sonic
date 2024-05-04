package ui

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.Client
import isDark

var currentTabIndex by mutableIntStateOf(0)

@Composable
fun TabBar() {
    Row {
        Switch(isDark, onCheckedChange = { isDark = it })
        TabRow(currentTabIndex, modifier = Modifier) {
            Client.currentTabs.values.forEach {
                Tab(it.index == currentTabIndex,
                    onClick = { currentTabIndex = it.index },
                    text = { Text(it.name) }
                )
            }
        }
    }
}