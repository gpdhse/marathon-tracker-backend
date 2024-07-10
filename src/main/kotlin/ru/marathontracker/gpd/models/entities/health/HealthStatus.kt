package ru.marathontracker.gpd.models.entities.health

import kotlinx.serialization.*

@Serializable
data class HealthStatus(
    @SerialName("user_id") val userId: String,
    @SerialName("max_pressure") val maxPressure: Int,
    @SerialName("min_pressure") val minPressure: Int,
    @SerialName("avg_pressure") val avgPressure: Int,
    val longitude: Double,
    val latitude: Double,
    val time: Long,
    val speed: Float,
    @SerialName("avg_speed") val avgSpeed: Float,
    val distance: Float,
)