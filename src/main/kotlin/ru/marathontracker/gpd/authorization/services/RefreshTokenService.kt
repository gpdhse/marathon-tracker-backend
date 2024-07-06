package ru.marathontracker.gpd.authorization.services

import ru.marathontracker.gpd.authorization.models.dtos.RefreshTokenDTO

interface RefreshTokenService {
    suspend fun create(refreshToken: RefreshTokenDTO): Result<String>
    suspend fun deleteByRefreshToken(refreshToken: String): Result<RefreshTokenDTO>
}