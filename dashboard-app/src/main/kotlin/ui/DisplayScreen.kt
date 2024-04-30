package ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.team9432.lib.dashboard.BooleanWidget
import org.team9432.lib.dashboard.DoubleWidget
import org.team9432.lib.dashboard.StringWidget
import org.team9432.lib.dashboard.WidgetData
import ui.colors.Colors
import ui.widgets.BooleanWidget
import ui.widgets.TextWidget

val valueMap = mutableStateMapOf<String, WidgetData>()

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
        is StringWidget -> TextWidget(value.name, value.value, enabled = value.allowDashboardEdit)
        is BooleanWidget -> BooleanWidget(value.name, value.value, enabled = value.allowDashboardEdit)
        is DoubleWidget -> TextWidget(value.name, value.value.toString(), enabled = value.allowDashboardEdit)

        null -> TextWidget(name, "missing value", enabled = true)
        else -> TextWidget(name, "Unsupported Type", enabled = true)
    }
}