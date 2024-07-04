package ru.marathontracker.gpd.data.services

import ru.marathontracker.gpd.data.models.dtos.UserDTO

interface UserService {
    suspend fun create(userDTO: UserDTO): Result<String>
    suspend fun read(id: String): Result<UserDTO>
    suspend fun update(id: String, userDTO: UserDTO): Result<UserDTO>
    suspend fun delete(id: String) : Result<UserDTO>
}