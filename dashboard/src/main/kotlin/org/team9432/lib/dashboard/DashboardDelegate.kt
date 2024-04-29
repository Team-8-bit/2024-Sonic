package org.team9432.lib.dashboard

import org.team9432.lib.dashboard.modules.BooleanModule
import org.team9432.lib.dashboard.modules.DoubleModule
import org.team9432.lib.dashboard.modules.TextModule
import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty

//class DashboardDelegate<T: ModuleBase>(title: String, initialValue: T) {
//    val callback = Dashboard.registerField(title, initialValue)
//
//    private var value: T = initialValue
//    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
//        return value
//    }
//
//    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
//        if (value != this.value) callback(value)
//        this.value = value
//    }
//}

fun textDashboardModule(title: String, initialValue: String): ReadWriteProperty<Any?, String> {
    val module = TextModule(title, initialValue)
    Dashboard.sendField(module)
    return Delegates.observable(initialValue) { _, _, newValue ->
        if (newValue != module.value) {
            module.value = newValue
            Dashboard.sendField(module)
        }
    }
}

fun doubleDashboardModule(title: String, initialValue: Double): ReadWriteProperty<Any?, Double> {
    val module = DoubleModule(title, initialValue)
    Dashboard.sendField(module)
    return Delegates.observable(initialValue) { _, _, newValue ->
        if (newValue != module.value) {
            module.value = newValue
            Dashboard.sendField(module)
        }
    }
}

fun booleanDashboardModule(title: String, initialValue: Boolean): ReadWriteProperty<Any?, Boolean> {
    val module = BooleanModule(title, initialValue)
    Dashboard.sendField(module)
    return Delegates.observable(initialValue) { _, _, newValue ->
        if (newValue != module.value) {
            module.value = newValue
            Dashboard.sendField(module)
        }
    }
}