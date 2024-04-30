package ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.sp
import org.team9432.lib.dashboard.*
import ui.colors.Colors

val valueMap = mutableStateMapOf<String, Type>()

@Composable
@Preview
fun DisplayScreen() {
    Surface(Modifier.fillMaxSize(), color = Colors.background) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                display("count")
                display("Alliance")
                Row {
                    display("Teleop")
                    display("Autonomous")
                    Column {
                        display("Disabled")
                        display("LEDTest")
                    }
                }
            }
        }
    }
}

@Composable
fun display(name: String) {
    when (val value = valueMap[name]) {
        is ImmutableBoolean -> immutableBooleanModule(value.name, value.value)
        is MutableBoolean -> mutableBooleanModule(value.name, value.value)
        is ImmutableString -> immutableTextModule(value.name, value.value)
        is ImmutableDouble -> immutableTextModule(value.name, value.value.toString())
        null -> immutableTextModule(name, "missing value")
        else -> immutableTextModule(name, "Unsupported Type")
    }
}

@Composable
fun immutableTextModule(name: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = name, color = Colors.text, fontSize = 20.sp)
        Text(text = value, color = Colors.text, fontSize = 15.sp, fontStyle = FontStyle.Italic)
    }
}