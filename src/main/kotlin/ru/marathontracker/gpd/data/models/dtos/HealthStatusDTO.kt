package ru.marathontracker.gpd.data.models.dtos

import org.bson.codecs.pojo.annotations.*
import org.bson.types.ObjectId

data class HealthStatusDTO(
    @BsonId val id: ObjectId = ObjectId(),
    @BsonProperty("user_id") val userId: ObjectId,
    val pulse: Int,
    val longitude: Double,
    val latitude: Double,
)
