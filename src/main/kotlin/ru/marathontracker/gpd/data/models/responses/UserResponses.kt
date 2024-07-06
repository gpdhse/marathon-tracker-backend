package ru.marathontracker.gpd.data.models.responses

import kotlinx.serialization.*

@Serializable
data class UserResponse(
    val id: String,
    val login: String,
)

@Serializable
data class CreateUserResponse(
    val message: String,
    val id: String,
)

@Serializable
data class ReadUserResponse(
    val message: String,
    val user: UserResponse,
)

@Serializable
data class UpdateUserResponse(
    val message: String,
    @SerialName("old_user") val oldUser: UserResponse,
)

@Serializable
data class DeleteUserResponse(
    val message: String,
    @SerialName("deleted_user") val deletedUser: UserResponse,
)