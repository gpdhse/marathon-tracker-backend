package ru.marathontracker.gpd.authorization.models.dtos

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class RefreshTokenDTO(
    @BsonId
    val id: ObjectId = ObjectId(),
    val refreshToken: String
)