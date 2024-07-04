package ru.marathontracker.gpd.data.models.requests

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserRequest(
    val login: String
)

@Serializable
data class UpdateUserRequest(
    val login: String,
)