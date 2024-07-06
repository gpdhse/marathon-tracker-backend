package ru.marathontracker.gpd.authorization.models.responses

import kotlinx.serialization.*
import ru.marathontracker.gpd.util.Sex

@Serializable
data class SignInResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String,
)

@Serializable
data class SignUpResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String,
)

@Serializable
data class AuthenticateResponse(
    val id: String,
    val email: String,
    val name: String,
    val age: Int,
    val sex: Sex,
    val height: Float,
    val weight: Float,
    val phone: String,
)

@Serializable
data class RefreshResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String,
)