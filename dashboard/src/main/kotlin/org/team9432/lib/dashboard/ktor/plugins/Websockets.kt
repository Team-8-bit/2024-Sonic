package org.team9432.lib.dashboard.ktor.plugins

import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.team9432.lib.dashboard.Dashboard
import org.team9432.lib.dashboard.Widget
import java.time.Duration
import java.util.*

object Websockets {
    private val connections = Collections.synchronizedSet(LinkedHashSet<WebSocketServerSession>())

    fun Application.configureSocket(path: String) {
        install(WebSockets) {
            pingPeriod = Duration.ofSeconds(15)
            timeout = Duration.ofSeconds(15)
            maxFrameSize = Long.MAX_VALUE
            masking = false
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
        }

        routing {
            webSocket(path) {
                try {
                    println("New connection: $this")
                    connections += this
                    while (true) {
                        val widget = receiveDeserialized<Widget>()
                        Dashboard.sendValue(widget)
                    }
                } finally {
                    connections -= this
                }
            }
        }
    }

    suspend fun sendToConnectedSockets(widget: Widget) = coroutineScope {
        connections.forEach { launch { it.sendSerialized(widget) } }
    }
}