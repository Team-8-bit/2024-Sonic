package org.team9432.lib.dashboard

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import org.team9432.lib.dashboard.server.Server
import kotlin.coroutines.CoroutineContext

object Dashboard {
    internal lateinit var coroutineContext: CoroutineContext
        private set
    private lateinit var coroutineScope: CoroutineScope

    suspend fun run(context: CoroutineContext): Unit = coroutineScope {
        Dashboard.coroutineContext = context
        coroutineScope = this

        Server.run()
    }

    private val currentValues = mutableMapOf<String, WidgetData>()

    fun sendValue(value: WidgetData) {
        currentValues[value.name] = value

        Server.sendToAll(value)
    }

    fun getValue(name: String): WidgetData? {
        return currentValues[name]
    }

    fun getAllWidgets(): List<WidgetData> {
        return currentValues.values.toList()
    }

}