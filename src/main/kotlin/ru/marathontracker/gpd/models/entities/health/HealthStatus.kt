package ru.marathontracker.gpd.models.entities.health

import kotlinx.serialization.*

@Serializable
data class HealthStatus(
    @SerialName("user_id") val userId: String,
    val pulse: Int,
    val longitude: Double,
    val latitude: Double,
)