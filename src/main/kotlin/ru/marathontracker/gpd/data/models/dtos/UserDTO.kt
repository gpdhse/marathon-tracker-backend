package ru.marathontracker.gpd.data.models.dtos

import kotlinx.serialization.Serializable

@Serializable
data class UserDTO(
    val login: String
)
