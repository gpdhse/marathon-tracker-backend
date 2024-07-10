package ru.marathontracker.gpd.data.models.dtos

import org.bson.codecs.pojo.annotations.*
import org.bson.types.ObjectId

data class HealthStatusDTO(
    @BsonId val id: ObjectId = ObjectId(),
    @BsonProperty("user_id") val userId: ObjectId,
    @BsonProperty("max_pressure") val maxPressure: Int,
    @BsonProperty("min_pressure") val minPressure: Int,
    @BsonProperty("avg_pressure") val avgPressure: Int,
    val longitude: Double,
    val latitude: Double,
    val time: Long,
    val speed: Float,
    @BsonProperty("avg_speed") val avgSpeed: Float,
    val distance: Float,
)
