package io

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import coroutineScope
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.kotlinx.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.team9432.lib.dashboard.Message

object Ktor {
    var connected by mutableStateOf(false)
        private set

    private val client = HttpClient {
        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
        }
    }

    private var session: DefaultClientWebSocketSession? = null

    var reconnectAttempt: Int = 0
    var reconnectCountdown by mutableIntStateOf(0)

    fun run() {
        coroutineScope.launch {
            while (true) {
                if (!connected) {
                    reconnectAttempt++
                    try {
                        session = client.webSocketSession(host = "localhost", port = 8080, path = "/timer")
                        connected = true
                    } catch (e: Exception) {
                        println("Error 1: ${e.message}")

                        connected = false
                        val delayTime = (1 * reconnectAttempt).coerceAtMost(5).coerceAtLeast(1)
                        println("Connection failed, retrying in $delayTime seconds.")

                        reconnectCountdown = delayTime

                        val increment = 1
                        do {
                            delay(increment * 1000L)
                            reconnectCountdown -= increment
                        } while (reconnectCountdown > 0)
                    }
                } else {
                    reconnectAttempt = 0
                    try {
                        while (session != null) {
                            val message = session?.receiveDeserialized<Message>() ?: continue
                            MessageProcessor.process(message)
                        }
                    } catch (e: Exception) {
                        println("Error 2: ${e.message}")
                    } finally {
                        session = null
                        connected = false
                    }
                }
            }
        }
    }
}