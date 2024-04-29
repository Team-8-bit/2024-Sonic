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
import org.team9432.lib.dashboard.modules.ModuleBase
import kotlin.coroutines.CoroutineContext

object Dashboard {
    lateinit var context: CoroutineContext
    fun start(context: CoroutineContext) {
        Dashboard.context = context
        embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module).start(wait = false)
    }


    val valueMap = mutableSetOf<ModuleBase>()

    @OptIn(DelicateCoroutinesApi::class)
    fun sendField(module: ModuleBase) {
        valueMap.add(module)

        GlobalScope.launch(context) {
            withTimeout(2000L) {
                Websockets.sendModuleToConnectedSockets(module)
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

//fun main() {
//    runBlocking {
//        Dashboard.start(this)
//    }
//}