package ru.marathontracker.gpd.data.models.dtos

import org.bson.codecs.pojo.annotations.*
import org.bson.types.ObjectId
import ru.marathontracker.gpd.authorization.security.hashing.SaltedHash

data class AdminDTO(
    @BsonId
    val id: ObjectId = ObjectId(),
    val email: String,
    val name: String,
    @BsonProperty("salted_hash") val saltedHash: SaltedHash,
)