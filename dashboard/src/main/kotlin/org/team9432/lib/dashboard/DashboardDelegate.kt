package org.team9432.lib.dashboard

import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun stringDashboardModule(title: String, initialValue: String): ReadWriteProperty<Any?, String> {
    var lastValue = initialValue
    Dashboard.sendValue(ImmutableString(title, initialValue))
    return Delegates.observable(initialValue) { _, _, newValue ->
        if (newValue != lastValue) {
            lastValue = newValue
            Dashboard.sendValue(ImmutableString(title, newValue))
        }
    }
}

fun doubleDashboardModule(title: String, initialValue: Double): ReadWriteProperty<Any?, Double> {
    var lastValue = initialValue
    Dashboard.sendValue(ImmutableDouble(title, initialValue))
    return Delegates.observable(initialValue) { _, _, newValue ->
        if (newValue != lastValue) {
            lastValue = newValue
            Dashboard.sendValue(ImmutableDouble(title, newValue))
        }
    }
}

fun booleanDashboardModule(title: String, initialValue: Boolean): ReadWriteProperty<Any?, Boolean> {
    var lastValue = initialValue
    Dashboard.sendValue(ImmutableBoolean(title, initialValue))
    return Delegates.observable(initialValue) { _, _, newValue ->
        if (newValue != lastValue) {
            lastValue = newValue
            Dashboard.sendValue(ImmutableBoolean(title, newValue))
        }
    }
}

class MutableBooleanDashboardModule(private val title: String, initialValue: Boolean) {
    init {
        Dashboard.sendValue(MutableBoolean(title, initialValue))
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Boolean {
        return (Dashboard.valueMap[title] as MutableBoolean).value
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {
        Dashboard.sendValue(MutableBoolean(title, value))
    }
}