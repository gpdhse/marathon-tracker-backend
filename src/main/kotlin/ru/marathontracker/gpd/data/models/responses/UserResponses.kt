package ru.marathontracker.gpd.data.models.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.marathontracker.gpd.data.models.dtos.UserDTO

interface UserResponse {
    val message: String
}

@Serializable
data class CreateUserResponse(
    override val message: String,
    val id: String,
) : UserResponse

@Serializable
data class ReadUserResponse(
    override val message: String,
    val user: UserDTO,
) : UserResponse

@Serializable
data class UpdateUserResponse(
    override val message: String,
    @SerialName("old_user") val oldUser: UserDTO,
) : UserResponse

@Serializable
data class DeleteUserResponse(
    override val message: String,
    @SerialName("deleted_user") val deletedUser: UserDTO,
) : UserResponse