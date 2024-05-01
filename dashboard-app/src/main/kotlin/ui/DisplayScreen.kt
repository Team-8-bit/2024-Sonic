package ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import io.Client
import org.team9432.lib.dashboard.server.sendable.BooleanWidget
import org.team9432.lib.dashboard.server.sendable.DoubleWidget
import org.team9432.lib.dashboard.server.sendable.StringWidget
import org.team9432.lib.dashboard.server.sendable.TabWidget
import ui.colors.Colors
import ui.widgets.BooleanWidget
import ui.widgets.TextWidget
import ui.widgets.WidgetBase

private val numberOfCols = 10 // X
private val numberOfRows = 6 // Y

var maxY by mutableFloatStateOf(0F)
var maxX by mutableFloatStateOf(0F)

@Composable
@Preview
fun DisplayScreen() {
    Surface(Modifier.fillMaxSize(), color = Colors.background) {
        Box(Modifier.fillMaxSize().onGloballyPositioned { maxY = it.size.height.toFloat(); maxX = it.size.width.toFloat() }) {
            Client.currentTabs.values.firstOrNull()?.data?.forEach { widgetData -> Widget(widgetData) }
        }
    }
}

@Composable
fun Widget(data: TabWidget) {
    val xPerUnit = maxX.pxToDp() / numberOfCols
    val yPerUnit = maxY.pxToDp() / numberOfRows

    WidgetBase(
        Modifier
            .width(xPerUnit * data.colsSpanned)
            .height(yPerUnit * data.rowsSpanned)
            .offset(x = xPerUnit * data.col, y = yPerUnit * data.row)
    ) {
        if (data.name != "%empty") {
            display(data.name)
        }
    }
}

@Composable
fun display(name: String) {
    when (val value = Client.getWidgetData(name)) {
        is StringWidget -> TextWidget(value.name, value.value, enabled = value.allowDashboardEdit)
        is BooleanWidget -> BooleanWidget(value.name, value.value, enabled = value.allowDashboardEdit)
        is DoubleWidget -> TextWidget(value.name, value.value.toString(), enabled = value.allowDashboardEdit)

        null -> TextWidget(name, "missing value", enabled = true)
        else -> TextWidget(name, "Unsupported Type", enabled = true)
    }
}

@Composable
fun Float.pxToDp() = (this / LocalDensity.current.density).dp