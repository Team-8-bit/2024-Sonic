import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import io.Client
import io.Ktor
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ui.DisconnectedScreen
import ui.DisplayScreen
import ui.TabBar
import ui.theme.AppTheme

var isDark by mutableStateOf(true)

@Composable
@Preview
fun App() {
    AppTheme(darkTheme = isDark) {
        // Either display the dashboard or wait for connection
        if (Ktor.connected) {
            Column {
                TabBar()
                DisplayScreen()
            }
        } else {
            DisconnectedScreen()
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
