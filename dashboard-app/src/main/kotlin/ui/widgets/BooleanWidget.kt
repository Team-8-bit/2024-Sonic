package ui.widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.sp
import io.Ktor
import org.team9432.lib.dashboard.BooleanWidget
import ui.colors.Colors

@Composable
fun BooleanWidget(name: String, value: Boolean, enabled: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = name, color = Colors.text, fontSize = 20.sp)
        Switch(checked = value, enabled = enabled, onCheckedChange = { isChecked ->
            if (enabled) {
                Ktor.sendType(BooleanWidget(name, isChecked, enabled))
            }
        })
    }
}