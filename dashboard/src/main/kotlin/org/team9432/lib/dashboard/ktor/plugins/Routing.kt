package org.team9432.lib.dashboard.ktor.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.team9432.lib.dashboard.Dashboard

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        get("/currentstate") {
            call.respond(Dashboard.valueMap.values)
        }
    }
}