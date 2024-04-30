package org.team9432.lib.dashboard.server

import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.team9432.lib.dashboard.Dashboard
import org.team9432.lib.dashboard.WidgetData
import java.util.*

internal object Websocket {
    private val connections = Collections.synchronizedSet(LinkedHashSet<WebSocketServerSession>())

    fun Application.configureSocket() {
        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
        }

        routing {
            webSocket("/socket") {
                try {
                    println("New connection: $this")
                    connections += this
                    while (true) {
                        val widgetData = receiveDeserialized<WidgetData>()
                        Dashboard.sendValue(widgetData)
                    }
                } finally {
                    connections -= this
                }
            }
        }
    }

    suspend fun sendToAll(widgetData: WidgetData) = coroutineScope {
        connections.forEach { launch { it.sendSerialized(widgetData) } }
    }
}