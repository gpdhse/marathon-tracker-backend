package ru.marathontracker.gpd.data.services.group

import ru.marathontracker.gpd.data.models.dtos.GroupDTO

interface GroupService {
    suspend fun create(group: GroupDTO): Result<String>
    suspend fun read(id: String): Result<GroupDTO>
    suspend fun update(group: GroupDTO): Result<GroupDTO>
    suspend fun delete(id: String): Result<GroupDTO>
    suspend fun addMember(groupId: String, memberId: String): Result<Unit>
}