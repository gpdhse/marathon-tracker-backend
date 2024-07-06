package ru.marathontracker.gpd.data.models.requests

import kotlinx.serialization.Serializable

@Serializable
data class CreateGroupRequest(
    val name: String,
    val description: String,
)

@Serializable
data class UpdateGroupRequest(
    val name: String,
    val description: String,
)
