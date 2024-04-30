package org.team9432.lib.dashboard

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import org.team9432.lib.dashboard.ktor.plugins.Websockets
import org.team9432.lib.dashboard.ktor.plugins.Websockets.configureSocket
import kotlin.coroutines.CoroutineContext

object Dashboard {
    private lateinit var context: CoroutineContext
    private lateinit var coroutineScope: CoroutineScope
    suspend fun run(context: CoroutineContext): Unit = coroutineScope {
        coroutineScope = this

        Dashboard.context = context
        embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
            install(ContentNegotiation) {
                json()
            }

            routing {
                get("/") { call.respondText("Hello World!") }
                get("/currentstate") { call.respond(valueMap.values) }
            }

            configureSocket("/socket")
        }.start(wait = false)
    }

    val valueMap = mutableMapOf<String, Widget>()

    fun sendValue(value: Widget) {
        valueMap[value.name] = value

        coroutineScope.launch(context) {
            withTimeout(2000L) {
                Websockets.sendToConnectedSockets(value)
            }
        }
    }
}