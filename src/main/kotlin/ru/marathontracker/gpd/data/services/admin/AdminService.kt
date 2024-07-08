package ru.marathontracker.gpd.data.services.admin

import ru.marathontracker.gpd.data.models.dtos.AdminDTO

interface AdminService {
    suspend fun create(admin: AdminDTO): Result<String>
    suspend fun read(id: String): Result<AdminDTO>
    suspend fun update(admin: AdminDTO): Result<AdminDTO>
    suspend fun delete(id: String): Result<AdminDTO>
    suspend fun findByEmail(email: String): Result<AdminDTO>
}