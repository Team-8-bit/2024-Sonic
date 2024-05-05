package org.team9432.dashboard.lib.delegates

import org.team9432.dashboard.lib.Dashboard
import org.team9432.dashboard.shared.BooleanWidget
import org.team9432.dashboard.shared.DoubleWidget
import org.team9432.dashboard.shared.StringWidget
import org.team9432.dashboard.shared.WidgetData
import kotlin.reflect.KProperty

fun stringDashboardWidget(title: String, initialValue: String, allowDashboardEdit: Boolean = false) =
    GenericWidget(title, initialValue, { StringWidget(title, it, allowDashboardEdit) }, { (it as StringWidget).value })

fun booleanDashboardWidget(title: String, initialValue: Boolean, allowDashboardEdit: Boolean = false) =
    GenericWidget(title, initialValue, { BooleanWidget(title, it, allowDashboardEdit) }, { (it as BooleanWidget).value })

fun doubleDashboardWidget(title: String, initialValue: Double, allowDashboardEdit: Boolean = false) =
    GenericWidget(title, initialValue, { DoubleWidget(title, it, allowDashboardEdit) }, { (it as DoubleWidget).value })

class GenericWidget<T>(private val title: String, initialValue: T, private val getWidget: (T) -> WidgetData, private val getValue: (WidgetData?) -> T) {
    init {
        Dashboard.updateWidget(getWidget(initialValue))
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return getValue(Dashboard.getWidgetData(title))
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        if (value != getValue(Dashboard.getWidgetData(title))) {
            Dashboard.updateWidget(getWidget(value))
        }
    }
}