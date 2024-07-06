package ru.marathontracker.gpd.routes.group

import io.ktor.server.routing.*

fun Routing.groupRoutes() = route("/group") {

}

private fun Route.createGroup() = post("/group") {

}

private fun Route.readGroup() = get("/group/{id}") {}

private fun Route.updateGroup() = patch("/group/{id}") {}

private fun Route.deleteGroup() = delete("/group/{id}") {}

private fun Route.joinGroup() = post("/group/{invite_code}") {}