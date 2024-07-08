package ru.marathontracker.gpd.models.responses.admin

import kotlinx.serialization.Serializable

@Serializable
data class AuthenticateResponse(
    val id: String,
    val email: String,
    val name: String,
)