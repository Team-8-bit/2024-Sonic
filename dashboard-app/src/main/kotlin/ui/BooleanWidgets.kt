package ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.sp
import io.Ktor
import org.team9432.lib.dashboard.MutableBoolean
import ui.colors.Colors

@Composable
fun immutableBooleanModule(name: String, value: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = name, color = Colors.text, fontSize = 20.sp)
        Switch(checked = value, enabled = false, onCheckedChange = {})
    }
}

@Composable
fun mutableBooleanModule(name: String, value: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = name, color = Colors.text, fontSize = 20.sp)
        Switch(checked = value, enabled = true, onCheckedChange = { isChecked ->
            Ktor.sendType(MutableBoolean(name, isChecked))
        })
    }
}