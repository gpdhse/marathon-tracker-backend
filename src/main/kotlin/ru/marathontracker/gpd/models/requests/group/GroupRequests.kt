package ru.marathontracker.gpd.models.requests.group

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
