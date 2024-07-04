package ru.marathontracker.gpd.data.mapper

import ru.marathontracker.gpd.data.models.dtos.UserDTO
import ru.marathontracker.gpd.data.models.requests.CreateUserRequest
import ru.marathontracker.gpd.data.models.requests.UpdateUserRequest

fun CreateUserRequest.toUserDTO() = UserDTO(login)

fun UpdateUserRequest.toUserDTO() = UserDTO(login)