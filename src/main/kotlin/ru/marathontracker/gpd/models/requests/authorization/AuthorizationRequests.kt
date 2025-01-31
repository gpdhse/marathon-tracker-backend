package ru.marathontracker.gpd.models.requests.authorization

import kotlinx.serialization.*
import ru.marathontracker.gpd.util.Sex

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
    val age: Int,
    val sex: Sex,
    val height: Int,
    val weight: Int,
    val phone: String,
    val password: String,
    @SerialName("device_id") val deviceId: String,
)

@Serializable
data class RefreshRequest(
    val token: String,
)