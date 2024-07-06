package ru.marathontracker.gpd.data.models.dtos

import io.ktor.server.auth.*
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import ru.marathontracker.gpd.authorization.security.hashing.SaltedHash
import ru.marathontracker.gpd.util.Sex

data class UserDTO(
    @BsonId
    val id: ObjectId = ObjectId(),
    val email: String,
    val name: String,
    val age: Int,
    val sex: Sex,
    val height: Float,
    val weight: Float,
    val phone: String,
    val saltedHash: SaltedHash,
) : Principal
