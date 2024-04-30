package org.team9432.lib.dashboard.server

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.team9432.lib.dashboard.Dashboard

fun Application.configureRoutes() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        get("/currentstate") {
            call.respond(Dashboard.getAllWidgets())
        }
    }
}