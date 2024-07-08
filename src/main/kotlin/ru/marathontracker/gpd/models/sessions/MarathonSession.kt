package ru.marathontracker.gpd.models.sessions

import kotlinx.serialization.*

@Serializable
data class MarathonSession(
    val username: String,
    @SerialName("account_type") val accountType: AccountType,
    @SerialName("session_id") val sessionId: String,
) {
    @Suppress("Unused")
    enum class AccountType { ADMIN, USER }
}
