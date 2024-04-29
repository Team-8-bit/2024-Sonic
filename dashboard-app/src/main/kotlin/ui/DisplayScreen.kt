package ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.sp
import org.team9432.lib.dashboard.modules.*
import ui.colors.Colors

val valueMap = mutableStateMapOf<String, Any?>()
var layout by mutableStateOf<ModuleGroup>(Col())

@Composable
@Preview
fun DisplayScreen() {
    Surface(Modifier.fillMaxSize(), color = Colors.background) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            displayGroup(layout)
        }
    }
}

@Composable
fun display(module: Module) {
    when (module) {
        is ModuleGroup -> displayGroup(module)
        is ModuleBase -> displayModule(module)
    }
}

@Composable
fun displayGroup(module: ModuleGroup) {
    when (module) {
        is Row -> {
            Row(verticalAlignment = Alignment.CenterVertically) {
                for (childModule in module.modules) {
                    display(childModule)
                }
            }
        }

        is Col -> {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                for (childModule in module.modules) {
                    display(childModule)
                }
            }
        }
    }
}

@Composable
fun displayModule(module: ModuleBase) {
    when (module) {
        is BooleanModule -> booleanModule(module)
        is DoubleModule -> doubleModule(module)
        is StringModule -> textModule(module)
    }
}

@Composable
fun booleanModule(module: BooleanModule) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = module.name, color = Colors.text, fontSize = 20.sp)
        Switch(checked = (valueMap[module.name] as? Boolean) ?: false, enabled = false, onCheckedChange = {})
    }
}

@Composable
fun textModule(module: StringModule) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = module.name, color = Colors.text, fontSize = 20.sp)
        Text(text = (valueMap[module.name] as? String) ?: "null", color = Colors.text, fontSize = 15.sp, fontStyle = FontStyle.Italic)
    }
}

@Composable
fun doubleModule(module: DoubleModule) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = module.name, color = Colors.text, fontSize = 20.sp)
        Text(text = (valueMap[module.name] as? Double)?.toString() ?: "null", color = Colors.text, fontSize = 15.sp, fontStyle = FontStyle.Italic)
    }
}