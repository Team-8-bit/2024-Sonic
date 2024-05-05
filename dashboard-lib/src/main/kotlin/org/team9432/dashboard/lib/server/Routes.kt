package org.team9432.dashboard.lib.server

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.team9432.dashboard.lib.Dashboard
import org.team9432.dashboard.shared.AddTab
import org.team9432.dashboard.shared.Sendable

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