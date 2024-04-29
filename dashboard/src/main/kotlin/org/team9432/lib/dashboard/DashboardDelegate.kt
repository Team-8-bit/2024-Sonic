package org.team9432.lib.dashboard

import org.team9432.lib.dashboard.modules.BooleanValueUpdateMessage
import org.team9432.lib.dashboard.modules.DoubleValueUpdateMessage
import org.team9432.lib.dashboard.modules.StringValueUpdateMessage
import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty

fun textDashboardModule(title: String, initialValue: String): ReadWriteProperty<Any?, String> {
    var lastValue = initialValue
    Dashboard.sendValue(StringValueUpdateMessage(title, initialValue))
    return Delegates.observable(initialValue) { _, _, newValue ->
        if (newValue != lastValue) {
            lastValue = newValue
            Dashboard.sendValue(StringValueUpdateMessage(title, newValue))
        }
    }
}

fun doubleDashboardModule(title: String, initialValue: Double): ReadWriteProperty<Any?, Double> {
    var lastValue = initialValue
    Dashboard.sendValue(DoubleValueUpdateMessage(title, initialValue))
    return Delegates.observable(initialValue) { _, _, newValue ->
        if (newValue != lastValue) {
            lastValue = newValue
            Dashboard.sendValue(DoubleValueUpdateMessage(title, newValue))
        }
    }
}

fun booleanDashboardModule(title: String, initialValue: Boolean): ReadWriteProperty<Any?, Boolean> {
    var lastValue = initialValue
    Dashboard.sendValue(BooleanValueUpdateMessage(title, initialValue))
    return Delegates.observable(initialValue) { _, _, newValue ->
        if (newValue != lastValue) {
            lastValue = newValue
            Dashboard.sendValue(BooleanValueUpdateMessage(title, newValue))
        }
    }
}