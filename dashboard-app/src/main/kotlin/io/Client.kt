package io

import androidx.compose.runtime.mutableStateMapOf
import kotlinx.coroutines.coroutineScope
import org.team9432.lib.dashboard.server.sendable.*

object Client {
    suspend fun run(): Unit = coroutineScope {
        Ktor.run()
    }

    fun processInformation(sendable: Sendable) {
        when (sendable) {
            is AddTab -> currentTabs[sendable.name] = sendable.tab
            is RemoveTab -> removeTab(sendable.name)
            is WidgetData -> valueMap[sendable.name] = sendable
        }
    }


    /* -------- Widgets -------- */

    private val valueMap = mutableStateMapOf<String, WidgetData>()

    fun getWidgetData(name: String) = valueMap[name]
    fun updateWidget(widgetData: WidgetData) = Ktor.send(widgetData)

    /* -------- Tabs -------- */

    val currentTabs = mutableStateMapOf<String, Tab>()

    private fun removeTab(name: String) {
        currentTabs.remove(name)
    }
}
