package ru.marathontracker.gpd.data.models.dtos

import kotlinx.serialization.SerialName
import org.bson.codecs.pojo.annotations.*
import org.bson.types.ObjectId

data class RefreshTokenDTO(
    @BsonId
    val id: ObjectId = ObjectId(),
    @BsonProperty("refresh_token") @SerialName("refresh_token") val refreshToken: String,
)