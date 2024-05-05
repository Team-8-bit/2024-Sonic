package ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import io.Client
import org.team9432.dashboard.shared.BooleanWidget
import org.team9432.dashboard.shared.DoubleWidget
import org.team9432.dashboard.shared.StringWidget
import org.team9432.dashboard.shared.TabWidget
import ui.TabBar
import ui.widgets.ImmutableBooleanWidget
import ui.widgets.MutableBooleanWidget
import ui.widgets.TextWidget
import ui.widgets.WidgetBase

// Number of rows and columns to display
private var numberOfCols = 10 // X
private var numberOfRows = 6 // Y

// X and Y dimensions of the widget area
var widgetAreaY by mutableFloatStateOf(0F)
var widgetAreaX by mutableFloatStateOf(0F)

/** Displays the given widgets. */
@Composable
fun DisplayScreen(widgets: List<TabWidget>) {
    Column {
        TabBar()
        Surface(Modifier.fillMaxSize()) {
            Box(Modifier.fillMaxSize().onGloballyPositioned { widgetAreaY = it.size.height.toFloat(); widgetAreaX = it.size.width.toFloat() }) {
                widgets.forEach { widgetData -> Widget(widgetData) }
            }
        }
    }
}

/** Displays a widget of a given size at the given position. */
@Composable
fun Widget(data: TabWidget) {
    val xPerUnit = widgetAreaX.pxToDp() / numberOfCols
    val yPerUnit = widgetAreaY.pxToDp() / numberOfRows

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

/** Finds and displays the correct type of widget by the given name. */
@Composable
fun display(name: String) {
    when (val value = Client.getWidgetData(name)) {
        is StringWidget -> TextWidget(value.name, value.value)
        is BooleanWidget -> {
            if (value.allowDashboardEdit) {
                MutableBooleanWidget(value.name, value.value)
            } else {
                ImmutableBooleanWidget(value.name, value.value)
            }
        }

        is DoubleWidget -> TextWidget(value.name, value.value.toString())

        null -> TextWidget(name, "missing value")
    }
}

/** Converts the value in px to dp. */
@Composable
fun Float.pxToDp() = (this / LocalDensity.current.density).dp