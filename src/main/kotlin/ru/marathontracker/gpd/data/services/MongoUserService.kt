package ru.marathontracker.gpd.data.services

import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import org.bson.types.ObjectId
import ru.marathontracker.gpd.data.models.dtos.UserDTO

class MongoUserService(database: MongoDatabase) : UserService {

    private val collection = database.getCollection<UserDTO>(COLLECTION_NAME)

    override suspend fun create(userDTO: UserDTO): Result<String> = withContext(Dispatchers.IO) {
        when (val id = collection.insertOne(userDTO).insertedId?.asObjectId()?.value?.toHexString()) {
            null -> Result.failure(NullPointerException("user cannot be inserted"))
            else -> Result.success(id)
        }
    }

    override suspend fun read(id: String): Result<UserDTO> = withContext(Dispatchers.IO) {
        when (val userDTO = collection.find(Filters.eq("_id", ObjectId(id))).firstOrNull()) {
            null -> Result.failure(NullPointerException("user cannot be found"))
            else -> Result.success(userDTO)
        }
    }

    override suspend fun update(id: String, userDTO: UserDTO): Result<UserDTO> = withContext(Dispatchers.IO) {
        when (val user = collection.findOneAndReplace(Filters.eq("_id", ObjectId(id)), userDTO)) {
            null -> Result.failure(NullPointerException("user cannot be updated"))
            else -> Result.success(user)
        }
    }

    override suspend fun delete(id: String): Result<UserDTO> = withContext(Dispatchers.IO) {
        when (val user = collection.findOneAndDelete(Filters.eq("_id", ObjectId(id)))) {
            null -> Result.failure(NullPointerException("user cannot be deleted"))
            else -> Result.success(user)
        }
    }

    private companion object {
        const val COLLECTION_NAME = "users"
    }
}