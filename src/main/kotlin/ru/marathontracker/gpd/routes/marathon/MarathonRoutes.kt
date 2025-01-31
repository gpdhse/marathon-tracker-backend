package ru.marathontracker.gpd.routes.marathon

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.json.Json
import ru.marathontracker.gpd.controllers.*
import ru.marathontracker.gpd.models.sessions.MarathonSession
import ru.marathontracker.gpd.util.MemberAlreadyExistsException

fun Routing.marathonRoutes(marathonController: MarathonController) = route("/marathon") {
    marathonSocket(marathonController)
    getAllStatuses(marathonController)
    sos()
}

private fun Route.marathonSocket(marathonController: MarathonController) = webSocket("/connection") {
    val session = call.sessions.get<MarathonSession>() ?: return@webSocket close(
        CloseReason(
            code = CloseReason.Codes.VIOLATED_POLICY,
            message = "No session"
        )
    )

    try {
        marathonController.onJoin(
            Member(
                userId = session.userId,
                accountType = session.accountType,
                sessionId = session.sessionId,
                socket = this
            )
        )
        incoming.consumeEach { frame ->
            (frame as? Frame.Text)?.let { text ->
                marathonController.sendStatus(
                    Json.decodeFromString(text.readText())
                )
            }
        }
    } catch (e: MemberAlreadyExistsException) {
        call.respond(HttpStatusCode.Conflict)
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        marathonController.tryDisconnect(session.userId)
    }
}

private fun Route.getAllStatuses(marathonController: MarathonController) = get("/messages"){
    call.respond(
        HttpStatusCode.OK,
        marathonController.getAllStatuses()
    )
}

private fun Route.sos() = get("/sos"){
    call.respond(HttpStatusCode.OK, "sos")
}
