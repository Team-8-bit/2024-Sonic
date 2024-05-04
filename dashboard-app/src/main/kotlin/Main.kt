import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import io.Client
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ui.AppState
import ui.NavRail
import ui.TabBar
import ui.screens.DisplayScreen
import ui.screens.SettingsScreen
import ui.theme.AppTheme

@Composable
@Preview
fun App() {
    AppTheme(darkTheme = AppState.isDarkMode) {
        Row {
            NavRail()
            Column {
                TabBar()
                when (AppState.screen) {
                    AppState.Screen.SETTINGS -> SettingsScreen()
                    AppState.Screen.DATA_VIEW -> DisplayScreen(Client.getWidgetsOnTab(AppState.currentTab))
                }
            }
        }
    }
}

fun main() {
    runBlocking {
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
