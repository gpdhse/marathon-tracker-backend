package ru.marathontracker.gpd.models.sessions

import kotlinx.serialization.*
import ru.marathontracker.gpd.util.AccountType

@Serializable
data class MarathonSession(
    val username: String,
    @SerialName("account_type") val accountType: AccountType,
    @SerialName("session_id") val sessionId: String,
) {
}
