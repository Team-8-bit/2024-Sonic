package org.team9432.lib.dashboard

import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty

fun textDashboardModule(title: String, initialValue: String): ReadWriteProperty<Any?, String> {
    var lastValue = initialValue
    Dashboard.sendValue(ValueUpdateMessage(title, ImmutableString(title, initialValue)))
    return Delegates.observable(initialValue) { _, _, newValue ->
        if (newValue != lastValue) {
            lastValue = newValue
            Dashboard.sendValue(ValueUpdateMessage(title, ImmutableString(title, newValue)))
        }
    }
}

fun doubleDashboardModule(title: String, initialValue: Double): ReadWriteProperty<Any?, Double> {
    var lastValue = initialValue
    Dashboard.sendValue(ValueUpdateMessage(title, ImmutableDouble(title, initialValue)))
    return Delegates.observable(initialValue) { _, _, newValue ->
        if (newValue != lastValue) {
            lastValue = newValue
            Dashboard.sendValue(ValueUpdateMessage(title, ImmutableDouble(title, newValue)))
        }
    }
}

fun booleanDashboardModule(title: String, initialValue: Boolean): ReadWriteProperty<Any?, Boolean> {
    var lastValue = initialValue
    Dashboard.sendValue(ValueUpdateMessage(title, ImmutableBoolean(title, initialValue)))
    return Delegates.observable(initialValue) { _, _, newValue ->
        if (newValue != lastValue) {
            lastValue = newValue
            Dashboard.sendValue(ValueUpdateMessage(title, ImmutableBoolean(title, newValue)))
        }
    }
}