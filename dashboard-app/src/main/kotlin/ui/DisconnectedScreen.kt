package ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.sp
import io.Ktor
import ui.colors.Colors

/** Displays the disconnected screen. This just has the countdown until the next connection attempt. */
@Composable
fun DisconnectedScreen() {
    Surface(Modifier.fillMaxSize(), color = Colors.background) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Disconnected", color = Colors.text)
                Text(text = "Reconnecting in ${Ktor.reconnectCountdown + 1}", color = Colors.text, fontStyle = FontStyle.Italic, fontSize = 13.sp)
            }
        }
    }
}