import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
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

@Composable
@Preview
fun App() {
    MaterialTheme {
        // Either display the dashboard or wait for connection
        if (Ktor.connected) {
            DisplayScreen()
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
