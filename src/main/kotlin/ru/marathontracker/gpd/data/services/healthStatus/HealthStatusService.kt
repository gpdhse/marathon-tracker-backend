package ru.marathontracker.gpd.data.services.healthStatus

import ru.marathontracker.gpd.data.models.dtos.HealthStatusDTO

interface HealthStatusService {

    suspend fun getAll(): Result<List<HealthStatusDTO>>

    suspend fun createOrReplace(healthStatus: HealthStatusDTO): Result<Unit>
}