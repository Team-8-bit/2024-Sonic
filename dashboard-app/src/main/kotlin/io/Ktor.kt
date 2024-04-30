package io

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.team9432.lib.dashboard.Widget
import ui.valueMap

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

    private var session: DefaultClientWebSocketSession? = null

    fun sendType(widget: Widget) {
        coroutineScope.launch { session?.sendSerialized(widget) }
    }

    private lateinit var coroutineScope: CoroutineScope

    suspend fun run() = coroutineScope {
        coroutineScope = this

        while (true) {
            val initialData = getInitialData()
            initialData.forEach { valueMap[it.name] = it }

            // Connect to the websocket
            val session = connectToWebsocket()
            connected = true
            this@Ktor.session = session

            // Receive and process information
            try {
                while (true) {
                    val message = session.receiveDeserialized<Widget>()
                    valueMap[message.name] = message
                }
            } catch (e: Exception) {
                println("Error while receiving: ${e.message}")
            }

            connected = false
            this@Ktor.session = null
        }
    }

    private suspend fun getInitialData(): List<Widget> {
        var reconnectAttempt = 0
        while (true) {
            try {
                return client.get("http://localhost:8080/currentstate").body()
            } catch (e: Exception) {
                println("Error while getting initial data: ${e.message}")

                reconnectAttempt++
                // Reconnect time gets longer as attempts go up, with a max of five seconds
                val delayTime = reconnectAttempt.coerceAtMost(5)

                println("Connection failed, retrying in $delayTime seconds.")

                runReconnectCountdown(delayTime)
            }
        }
    }

    private suspend fun connectToWebsocket(): DefaultClientWebSocketSession {
        var reconnectAttempt = 0
        while (true) {
            try {
                return client.webSocketSession(host = "localhost", port = 8080, path = "/socket")
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
            reconnectCountdown -= 1
            delay(1000)
        } while (reconnectCountdown > 0)
    }
}