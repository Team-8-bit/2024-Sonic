package io

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import coroutineScope
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.team9432.lib.dashboard.ValueUpdateMessage

object Ktor {
    var connected by mutableStateOf(false)
        private set

    private val client = HttpClient {
        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
        }
        install(ContentNegotiation) {
            json()
        }
    }

    var reconnectCountdown by mutableIntStateOf(0)

    fun run() {
        coroutineScope.launch {
            while (true) {
                val initialData = getInitialData()
                initialData.forEach { MessageProcessor.process(it) }

                // Connect to the websocket
                val session = connectToWebsocket()
                connected = true

                // Receive and process information
                try {
                    while (true) {
                        val message = session.receiveDeserialized<ValueUpdateMessage>()
                        MessageProcessor.process(message)
                    }
                } catch (e: Exception) {
                    println("Error while receiving: ${e.message}")
                } finally {
                    connected = false
                }
            }
        }
    }

    private suspend fun getInitialData(): List<ValueUpdateMessage> {
        while (true) {
            try {
                return client.get("http://localhost:8080/currentstate").body()
            } catch (e: Exception) {
                println("Error while getting initial data: ${e.message}")
                delay(1000)
            }
        }
    }

    private suspend fun connectToWebsocket(): DefaultClientWebSocketSession {
        var reconnectAttempt = 0
        while (true) {
            try {
                return client.webSocketSession(host = "localhost", port = 8080, path = "/timer")
            } catch (e: Exception) {
                println("Error while connecting: ${e.message}")

                reconnectAttempt++
                // Reconnect time gets longer as attempts go up, with a max of five seconds
                val delayTime = reconnectAttempt.coerceAtMost(5)

                println("Connection failed, retrying in $delayTime seconds.")

                runReconnectCountdown(delayTime)
            }
        }
    }

    private suspend fun runReconnectCountdown(delayTime: Int) {
        reconnectCountdown = delayTime

        do {
            delay(1000)
            reconnectCountdown -= 1
        } while (reconnectCountdown > 0)
    }
}