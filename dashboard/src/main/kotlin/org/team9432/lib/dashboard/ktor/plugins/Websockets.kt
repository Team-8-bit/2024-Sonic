package org.team9432.lib.dashboard.ktor.plugins

import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.team9432.lib.dashboard.Dashboard
import org.team9432.lib.dashboard.modules.InitialMessage
import org.team9432.lib.dashboard.modules.LayoutMessage
import org.team9432.lib.dashboard.modules.Message
import org.team9432.lib.dashboard.modules.ModuleGroup
import java.time.Duration
import java.util.*

object Websockets {
    private val connections = Collections.synchronizedSet(LinkedHashSet<WebSocketServerSession>())

    fun Application.configureSockets() {
        install(WebSockets) {
            pingPeriod = Duration.ofSeconds(15)
            timeout = Duration.ofSeconds(15)
            maxFrameSize = Long.MAX_VALUE
            masking = false
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
        }

        routing {
            webSocket("/timer") {
                try {
                    println("New connection: $this")
                    initializeConnection(this)
                    connections += this
                    for (frame in incoming) {
                        // Do nothing
                    }
                } finally {
                    connections -= this
                }
            }
        }
    }

    suspend fun initializeConnection(connection: WebSocketServerSession) {
        connection.sendSerialized<Message>(
            InitialMessage(Dashboard.valueMap.values.toMutableList<Message>().also { list -> Dashboard.currentLayout?.let { layout -> list.add(0, LayoutMessage(layout)) } })
        )
    }

    suspend fun sendToConnectedSockets(message: Message) = coroutineScope {
        connections.forEach { launch { it.sendSerialized(message) } }
    }

    suspend fun sendLayoutToConnectedSockets(layout: ModuleGroup) {
        sendToConnectedSockets(LayoutMessage(layout))
    }
}