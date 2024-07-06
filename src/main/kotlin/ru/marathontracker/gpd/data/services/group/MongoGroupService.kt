package ru.marathontracker.gpd.data.services.group

import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.firstOrNull
import org.bson.types.ObjectId
import ru.marathontracker.gpd.data.models.dtos.GroupDTO
import ru.marathontracker.gpd.util.ID_FIELD

class MongoGroupService(database: MongoDatabase) : GroupService {
    private val collection: MongoCollection<GroupDTO> = database.getCollection<GroupDTO>(COLLECTION_NAME)

    override suspend fun create(group: GroupDTO): Result<String> = withContext(Dispatchers.IO) {
        when (val id = collection.insertOne(group).insertedId?.asObjectId()?.value?.toHexString()) {
            null -> Result.failure(NullPointerException("group cannot be inserted"))
            else -> Result.success(id)
        }
    }

    override suspend fun read(id: String): Result<GroupDTO> = withContext(Dispatchers.IO) {
        when (val group = collection.find(Filters.eq(ID_FIELD, ObjectId(id))).firstOrNull()) {
            null -> Result.failure(NullPointerException("group do not exist"))
            else -> Result.success(group)
        }
    }

    override suspend fun update(group: GroupDTO): Result<GroupDTO> = withContext(Dispatchers.IO) {
        when (val oldGroup = collection.findOneAndReplace(Filters.eq(ID_FIELD, group.id), group)) {
            null -> Result.failure(NullPointerException("group cannot be updated"))
            else -> Result.success(oldGroup)
        }
    }

    override suspend fun delete(id: String): Result<GroupDTO> = withContext(Dispatchers.IO) {
        when (val oldGroup = collection.findOneAndDelete(Filters.eq(ID_FIELD, ObjectId(id)))) {
            null -> Result.failure(NullPointerException("group cannot be deleted"))
            else -> Result.success(oldGroup)
        }
    }

    override suspend fun addMember(groupId: String, memberId: String): Result<Unit> = withContext(Dispatchers.IO) {
        val oldGroup =
            (collection.find(Filters.eq(ID_FIELD, ObjectId(groupId))).firstOrNull() ?: return@withContext Result.failure(
                NullPointerException("group do not exist")
            ))
        val newMembers = oldGroup.members.toMutableSet().apply { this.add(ObjectId(memberId)) }.toSet()
        val newGroup = oldGroup.copy(members = newMembers)
        when (update(newGroup).getOrNull()) {
            null -> Result.failure(NullPointerException("group cannot be updated"))
            else -> Result.success(Unit)
        }
    }

    private companion object{
        private const val COLLECTION_NAME = "groups"
    }
}