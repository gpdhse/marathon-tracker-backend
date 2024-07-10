package ru.marathontracker.gpd.plugins

import io.ktor.server.application.*
import io.ktor.server.application.ApplicationCallPipeline.ApplicationPhase.Plugins
import io.ktor.server.sessions.*
import kotlinx.serialization.json.Json
import ru.marathontracker.gpd.models.sessions.MarathonSession
import ru.marathontracker.gpd.util.AccountType

fun Application.configureSessions() {
    install(Sessions){
        cookie<MarathonSession>("SESSION")
    }

    intercept(Plugins){
        if(call.sessions.get<MarathonSession>() == null){
            val username = call.parameters["username"] ?: return@intercept
            val accountType = Json.decodeFromString<AccountType>(call.parameters["accountType"] ?: return@intercept)
            call.sessions.set(MarathonSession(username, accountType, generateSessionId()))
        }
    }
}