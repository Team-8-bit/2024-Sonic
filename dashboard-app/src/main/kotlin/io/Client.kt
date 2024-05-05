package io

import androidx.compose.runtime.mutableStateMapOf
import org.team9432.dashboard.shared.*

/** The interface between the app and the networking code. Also tracks the current state of all widgets and tabs. */
object Client {
    /** Starts and runs the client, must be called before anything else. */
    suspend fun run() = Ktor.run()

    /** Process a newly received piece of information. */
    fun processInformation(sendable: Sendable) {
        when (sendable) {
            is AddTab -> currentTabs[sendable.name] = sendable.tab
            is RemoveTab -> removeTab(sendable.name)
            is WidgetData -> valueMap[sendable.name] = sendable
        }
    }

    /** Forces the current connection to restart. */
    fun reconnect() = Ktor.reconnect()

    /* -------- Widgets -------- */

    /** Map of current widget states. */
    private val valueMap = mutableStateMapOf<String, WidgetData>()

    /** Gets the widget data with a given name. */
    fun getWidgetData(name: String) = valueMap[name]

    /** Updates the state of a widget by sending it to the robot code. */
    fun updateWidget(widgetData: WidgetData) = Ktor.send(widgetData)

    /* -------- Tabs -------- */

    /** A map of the current tabs. */
    val currentTabs = mutableStateMapOf<String, Tab>()

    /** Removes a tab. */
    private fun removeTab(name: String) {
        currentTabs.remove(name)
    }

    /** Gets the widgets that should be displayed on the given tab. */
    fun getWidgetsOnTab(index: Int): List<TabWidget> {
        return currentTabs.values.firstOrNull { it.index == index }?.data ?: emptyList()
    }
}
