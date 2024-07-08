package ru.marathontracker.gpd.data.services.refreshToken

import ru.marathontracker.gpd.data.models.dtos.RefreshTokenDTO

interface RefreshTokenService {
    suspend fun create(refreshToken: RefreshTokenDTO): Result<String>
    suspend fun deleteByRefreshToken(refreshToken: String): Result<RefreshTokenDTO>
}