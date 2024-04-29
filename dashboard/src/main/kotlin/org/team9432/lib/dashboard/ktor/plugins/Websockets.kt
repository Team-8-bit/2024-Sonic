package org.team9432.lib.dashboard.ktor.plugins

import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.team9432.lib.dashboard.Dashboard
import org.team9432.lib.dashboard.InitialMessage
import org.team9432.lib.dashboard.Message
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
        connection.sendSerialized<Message>(InitialMessage(Dashboard.valueMap.values.toList<Message>()))
    }

    suspend fun sendToConnectedSockets(message: Message) = coroutineScope {
        connections.forEach { launch { it.sendSerialized(message) } }
    }
}