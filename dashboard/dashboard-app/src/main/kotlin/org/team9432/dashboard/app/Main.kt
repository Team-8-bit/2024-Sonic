package org.team9432.dashboard.app

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.team9432.dashboard.app.io.Client
import org.team9432.dashboard.app.io.Config
import org.team9432.dashboard.app.ui.AppState
import org.team9432.dashboard.app.ui.NavRail
import org.team9432.dashboard.app.ui.screens.DisplayScreen
import org.team9432.dashboard.app.ui.screens.SettingsScreen
import org.team9432.dashboard.app.ui.theme.AppTheme

@Composable
@Preview
fun App() {
    AppTheme(darkTheme = AppState.isDarkMode) {
        Row {
            NavRail()
            when (AppState.screen) {
                AppState.Screen.SETTINGS -> SettingsScreen()
                AppState.Screen.DATA_VIEW -> DisplayScreen(Client.getWidgetsOnTab(AppState.currentTab))
            }
        }
    }
}

fun main() {
    runBlocking {
        Config.refreshAppStateFromConfig()

        launch {
            Client.run()
        }

        application {
            Window(onCloseRequest = ::exitApplication, title = "Dashboard", state = WindowState(WindowPlacement.Maximized)) {
                App()
            }
        }
    }
}
