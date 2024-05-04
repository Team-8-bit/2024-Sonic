package org.team9432.lib.dashboard

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import org.team9432.lib.dashboard.server.Server
import org.team9432.lib.dashboard.server.sendable.*
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

    fun processInformation(sendable: Sendable) {
        when (sendable) {
            is WidgetData -> currentValues[sendable.name] = sendable
            else -> {}
        }
    }


    /* -------- Widgets -------- */

    private val currentValues = mutableMapOf<String, WidgetData>()

    fun updateWidget(value: WidgetData) {
        currentValues[value.name] = value
        Server.sendToAll(value)
    }

    fun getWidgetData(name: String) = currentValues[name]
    fun getAllWidgetData() = currentValues.values.toList()


    /* -------- Tabs -------- */

    private val currentTabs = mutableMapOf<String, Tab>()

    fun addTab(tab: Tab) {
        assert(currentTabs.none { it.value.index == tab.index }) { "There is already a tab at index ${tab.index}!" }
        currentTabs[tab.name] = tab
        Server.sendToAll(AddTab(tab.name, tab))
    }

    fun removeTab(name: String) {
        currentTabs.remove(name)
        Server.sendToAll(RemoveTab(name))
    }

    fun getAllTabs() = currentTabs.values.toList()
}