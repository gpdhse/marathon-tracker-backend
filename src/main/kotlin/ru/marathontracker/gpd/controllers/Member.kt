package ru.marathontracker.gpd.controllers

import io.ktor.websocket.*
import ru.marathontracker.gpd.util.AccountType

data class Member(
    val username: String,
    val accountType: AccountType,
    val sessionId: String,
    val socket: WebSocketSession,
)
