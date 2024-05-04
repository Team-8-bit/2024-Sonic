package ui

import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.Client

var currentTabIndex by mutableIntStateOf(0)

@Composable
fun TabBar() {
    TabRow(currentTabIndex, modifier = Modifier) {
        Client.currentTabs.values.forEach {
            Tab(it.index == currentTabIndex,
                onClick = { currentTabIndex = it.index },
                text = { Text(it.name) }
            )
        }
    }
}