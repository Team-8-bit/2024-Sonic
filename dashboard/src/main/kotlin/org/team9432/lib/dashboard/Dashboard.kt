package org.team9432.lib.dashboard

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import org.team9432.lib.dashboard.ktor.plugins.*
import org.team9432.lib.dashboard.ktor.plugins.Websockets.configureSockets
import kotlin.coroutines.CoroutineContext

object Dashboard {
    private lateinit var context: CoroutineContext
    fun start(context: CoroutineContext) {
        Dashboard.context = context
        embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module).start(wait = false)
    }

    val valueMap = mutableMapOf<String, ValueUpdateMessage>()

    @OptIn(DelicateCoroutinesApi::class)
    fun sendValue(message: ValueUpdateMessage) {
        valueMap[message.key] = message

        GlobalScope.launch(context) {
            withTimeout(2000L) {
                Websockets.sendToConnectedSockets(message)
            }
        }
    }
}

fun Application.module() {
    configureHTTP()
    configureSerialization()
    configureTemplating()
    configureRouting()
    configureSockets()
}