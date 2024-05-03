package org.team9432.lib.dashboard.server

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.team9432.lib.dashboard.Dashboard
import org.team9432.lib.dashboard.server.sendable.AddTab
import org.team9432.lib.dashboard.server.sendable.Sendable

fun Application.configureRoutes() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        get("/currentstate") {
            val widgetData = Dashboard.getAllWidgetData() as List<Sendable>
            val tabData = Dashboard.getAllTabs().map { AddTab(it.name, it) } as List<Sendable>
            call.respond(widgetData + tabData)
        }
    }
}