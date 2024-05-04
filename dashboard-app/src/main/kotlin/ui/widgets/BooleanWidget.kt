package ui.widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import io.Client
import org.team9432.lib.dashboard.server.sendable.BooleanWidget

/** Boolean displayed as a toggle switch. */
@Composable
fun BooleanWidget(name: String, value: Boolean, enabled: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = name, fontSize = 20.sp, textAlign = TextAlign.Center)
        Switch(checked = value, enabled = enabled, onCheckedChange = { isChecked ->
            if (enabled) {
                Client.updateWidget(BooleanWidget(name, isChecked, enabled))
            }
        })
    }
}