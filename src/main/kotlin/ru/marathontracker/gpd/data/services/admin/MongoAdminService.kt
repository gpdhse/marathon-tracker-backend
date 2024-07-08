package ru.marathontracker.gpd.data.services.admin

import com.mongodb.client.model.*
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.firstOrNull
import org.bson.types.ObjectId
import ru.marathontracker.gpd.data.models.dtos.AdminDTO
import ru.marathontracker.gpd.util.ID_FIELD

class MongoAdminService(database: MongoDatabase) : AdminService {

    private val collection = database.getCollection<AdminDTO>(COLLECTION_NAME)

    override suspend fun create(admin: AdminDTO): Result<String> = withContext(Dispatchers.IO) {
        collection.createIndex(Indexes.descending(AdminDTO::email.name), IndexOptions().unique(true))
        when (val id = collection.insertOne(admin).insertedId?.asObjectId()?.value?.toHexString()) {
            null -> Result.failure(NullPointerException("Admin cannot be inserted"))
            else -> Result.success(id)
        }
    }

    override suspend fun read(id: String): Result<AdminDTO> = withContext(Dispatchers.IO) {
        when (val admin = collection.find(Filters.eq(ID_FIELD, ObjectId(id))).firstOrNull()) {
            null -> Result.failure(NullPointerException("Admin cannot be found"))
            else -> Result.success(admin)
        }
    }

    override suspend fun update(admin: AdminDTO): Result<AdminDTO> = withContext(Dispatchers.IO) {
        when (val oldAdmin = collection.findOneAndReplace(Filters.eq(ID_FIELD, admin.id), admin)) {
            null -> Result.failure(NullPointerException("Admin cannot be updated"))
            else -> Result.success(oldAdmin)
        }
    }

    override suspend fun delete(id: String): Result<AdminDTO> = withContext(Dispatchers.IO) {
        when (val oldAdmin = collection.findOneAndDelete(Filters.eq(ID_FIELD, ObjectId(id)))) {
            null -> Result.failure(NullPointerException("Admin cannot be deleted"))
            else -> Result.success(oldAdmin)
        }
    }

    override suspend fun findByEmail(email: String): Result<AdminDTO> = withContext(Dispatchers.IO) {
        when (val admin = collection.find(Filters.eq(AdminDTO::email.name, email)).firstOrNull()) {
            null -> Result.failure(NullPointerException("Admin cannot be found"))
            else -> Result.success(admin)
        }
    }

    private companion object {
        private const val COLLECTION_NAME = "admins"
    }
}