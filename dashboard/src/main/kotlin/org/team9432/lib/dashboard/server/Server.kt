package org.team9432.lib.dashboard.server

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.team9432.lib.dashboard.Dashboard
import org.team9432.lib.dashboard.WidgetData
import org.team9432.lib.dashboard.server.Websocket.configureSocket

object Server {
    private lateinit var coroutineScope: CoroutineScope

    suspend fun run(): Unit = coroutineScope {
        coroutineScope = this

        embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
            install(ContentNegotiation) {
                json()
            }

            configureRoutes()
            configureSocket()
        }.start(wait = false)
    }

    fun sendToAll(value: WidgetData) {
        coroutineScope.launch(Dashboard.coroutineContext) {
            Websocket.sendToAll(value)
        }
    }
}