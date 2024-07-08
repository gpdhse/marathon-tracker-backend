package ru.marathontracker.gpd.models.requests.admin

import kotlinx.serialization.*

@Serializable
data class SignInRequest(
    val email: String,
    val password: String,
    @SerialName("device_id") val deviceId: String,
)

@Serializable
data class SignUpRequest(
    val email: String,
    val name: String,
    val password: String,
    @SerialName("device_id") val deviceId: String,
)