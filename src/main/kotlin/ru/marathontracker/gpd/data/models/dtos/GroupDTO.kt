package ru.marathontracker.gpd.data.models.dtos

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class GroupDTO(
    @BsonId
    val id: ObjectId = ObjectId(),
    val name: String,
    val description: String,
    val inviteCode: String,
    val members: Set<ObjectId> = emptySet(),
    val marathon: ObjectId? = null,
)