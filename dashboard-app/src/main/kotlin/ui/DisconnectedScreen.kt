package ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.sp
import io.Ktor

/** Displays the disconnected screen. This just has the countdown until the next connection attempt. */
@Composable
fun DisconnectedScreen() {
    Surface(Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Disconnected")
                Text(text = "Reconnecting in ${Ktor.reconnectCountdown + 1}", fontStyle = FontStyle.Italic, fontSize = 13.sp)
            }
        }
    }
}