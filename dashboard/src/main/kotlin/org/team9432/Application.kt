package org.team9432

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.team9432.plugins.*

object Dashboard {
    @OptIn(DelicateCoroutinesApi::class)
    fun start(): Job {
        return GlobalScope.launch {
            embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module).start(wait = true)
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

suspend fun main() {
    Dashboard.start().join()
}