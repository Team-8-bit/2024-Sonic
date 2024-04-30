package ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import org.team9432.lib.dashboard.BooleanWidget
import org.team9432.lib.dashboard.DoubleWidget
import org.team9432.lib.dashboard.StringWidget
import org.team9432.lib.dashboard.WidgetData
import ui.colors.Colors
import ui.widgets.BooleanWidget
import ui.widgets.TextWidget
import ui.widgets.WidgetBase

val valueMap = mutableStateMapOf<String, WidgetData>()

private val numberOfCols = 10 // X
private val numberOfRows = 6 // Y

var maxY by mutableFloatStateOf(0F)
var maxX by mutableFloatStateOf(0F)

@Composable
@Preview
fun DisplayScreen() {
    Surface(Modifier.fillMaxSize(), color = Colors.background) {
        Box(Modifier.fillMaxSize().onGloballyPositioned { maxY = it.size.height.toFloat(); maxX = it.size.width.toFloat() }) {
            Widget(0, 0, "count")
            Widget(0, 1, "Teleop")
            Widget(1, 0, "Disabled", rowsSpanned = 2)
            Widget(1, 1, "Alliance", colsSpanned = 2)
            Widget(2, 1, "Autonomous")
            Widget(0, 2, "LEDTest")
        }
    }
}

@Composable
fun Widget(row: Int, col: Int, name: String, rowsSpanned: Int = 1, colsSpanned: Int = 1) {
    val xPerUnit = maxX.pxToDp() / numberOfCols
    val yPerUnit = maxY.pxToDp() / numberOfRows

    WidgetBase(
        Modifier
            .width(xPerUnit * colsSpanned)
            .height(yPerUnit * rowsSpanned)
            .offset(x = xPerUnit * col, y = yPerUnit * row)
    ) {
        if (name != "%empty") {
            display(name)
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

@Composable
fun Float.pxToDp() = (this / LocalDensity.current.density).dp