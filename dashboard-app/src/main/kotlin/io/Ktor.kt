package io

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
import org.team9432.lib.dashboard.server.sendable.Sendable
import ui.AppState

object Ktor {
    /** The [HttpClient] instance used by the dashboard. */
    private val client = HttpClient {
        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
        }
        install(ContentNegotiation) {
            json()
        }
    }

    private var session: DefaultClientWebSocketSession? = null

    /** Sends the given [Sendable] to the robot code. */
    fun send(sendable: Sendable) {
        coroutineScope.launch { session?.sendSerialized(sendable) }
    }

    private lateinit var coroutineScope: CoroutineScope

    /** Starts and runs the client, must be called before anything else. */
    suspend fun run() = coroutineScope {
        coroutineScope = this

        while (true) {
            // Get the current state of everything from the robot code
            val initialData = getInitialData()
            initialData.forEach { Client.processInformation(it) }

            // Connect to the websocket
            val session = connectToWebsocket()
            AppState.connected = true
            this@Ktor.session = session

            // Receive and process information
            try {
                while (true) {
                    val sendable = session.receiveDeserialized<Sendable>()
                    Client.processInformation(sendable)
                }
            } catch (e: Exception) {
                println("Error while receiving: ${e.message}")
            }

            AppState.connected = false
            this@Ktor.session = null
        }
    }

    /** Gets the page of initial information from the robot. */
    private suspend fun getInitialData(): List<Sendable> {
        var reconnectAttempt = 0
        while (true) {
            try {
                // Attempt to get the page
                return client.get("http://localhost:8080/currentstate").body()
            } catch (e: Exception) { // If it didn't work, wait a bit and try again
                println("Error while getting initial data: ${e.message}")

                reconnectAttempt++
                // Reconnect time gets longer as attempts go up, with a max of five seconds
                val delayTime = reconnectAttempt.coerceAtMost(5)

                println("Connection failed, retrying in $delayTime seconds.")

                delay(delayTime * 1000L)
            }
        }
    }

    /** Connects to the robot websocket and returns the running session. */
    private suspend fun connectToWebsocket(): DefaultClientWebSocketSession {
        var reconnectAttempt = 0
        while (true) {
            try {
                // Attempt to connect
                return client.webSocketSession(host = "localhost", port = 8080, path = "/socket")
            } catch (e: Exception) { // If it didn't work, wait a bit and try again
                println("Error while connecting: ${e.message}")

                reconnectAttempt++
                // Reconnect time gets longer as attempts go up, with a max of five seconds
                val delayTime = reconnectAttempt.coerceAtMost(5)

                println("Connection failed, retrying in $delayTime seconds.")

                delay(delayTime * 1000L)
            }
        }
    }
}