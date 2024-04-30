import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.Ktor
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ui.DisconnectedScreen
import ui.DisplayScreen

@Composable
@Preview
fun App() {
    MaterialTheme {
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
            Ktor.run()
        }

        application {
            Window(onCloseRequest = ::exitApplication, title = "Dashboard") {
                App()
            }
        }
    }
}
