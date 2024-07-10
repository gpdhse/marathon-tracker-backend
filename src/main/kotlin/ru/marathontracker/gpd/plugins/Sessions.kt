package ru.marathontracker.gpd.plugins

import io.ktor.server.application.*
import io.ktor.server.application.ApplicationCallPipeline.ApplicationPhase.Plugins
import io.ktor.server.sessions.*
import kotlinx.serialization.json.Json
import ru.marathontracker.gpd.models.sessions.MarathonSession
import ru.marathontracker.gpd.util.AccountType

fun Application.configureSessions() {
    install(Sessions) {
        cookie<MarathonSession>("SESSION")
    }

    intercept(Plugins) {
        if (call.sessions.get<MarathonSession>() == null) {
            val userId = call.parameters["user_id"] ?: return@intercept
            val accountType =
                runCatching {
                    Json.decodeFromString<AccountType>(
                        string = call.parameters["account_type"] ?: return@intercept
                    )
                }.getOrElse { return@intercept }
            call.sessions.set(MarathonSession(userId, accountType, generateSessionId()))
        }
    }
}