package ru.marathontracker.gpd.routes.admin

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.adminRoutes() = route("/admin") {
    sos()
}

fun Route.sos() = authenticate {
    post("/sos") {
        val principal = call.principal<JWTPrincipal>()!!
        val id = principal.getClaim("id", String::class)!!
        call.respond(HttpStatusCode.OK, "sos")
    }
}