package ru.marathontracker.gpd.data.services.user

import com.mongodb.client.model.*
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.firstOrNull
import org.bson.types.ObjectId
import ru.marathontracker.gpd.data.models.dtos.UserDTO
import ru.marathontracker.gpd.util.ID_FIELD

class MongoUserService(database: MongoDatabase) : UserService {

    private val collection = database.getCollection<UserDTO>(COLLECTION_NAME)

    override suspend fun create(userDTO: UserDTO): Result<String> = withContext(Dispatchers.IO) {
        collection.createIndex(Indexes.descending(UserDTO::email.name), IndexOptions().unique(true))
        when (val id = collection.insertOne(userDTO).insertedId?.asObjectId()?.value?.toHexString()) {
            null -> Result.failure(NullPointerException("User cannot be inserted"))
            else -> Result.success(id)
        }
    }

    override suspend fun read(id: String): Result<UserDTO> = withContext(Dispatchers.IO) {
        when (val userDTO = collection.find(Filters.eq(ID_FIELD, ObjectId(id))).firstOrNull()) {
            null -> Result.failure(NullPointerException("User cannot be found"))
            else -> Result.success(userDTO)
        }
    }

    override suspend fun update(id: String, userDTO: UserDTO): Result<UserDTO> = withContext(Dispatchers.IO) {
        when (val user = collection.findOneAndReplace(Filters.eq(ID_FIELD, ObjectId(id)), userDTO)) {
            null -> Result.failure(NullPointerException("User cannot be updated"))
            else -> Result.success(user)
        }
    }

    override suspend fun delete(id: String): Result<UserDTO> = withContext(Dispatchers.IO) {
        when (val user = collection.findOneAndDelete(Filters.eq(ID_FIELD, ObjectId(id)))) {
            null -> Result.failure(NullPointerException("User cannot be deleted"))
            else -> Result.success(user)
        }
    }

    override suspend fun findByEmail(email: String): Result<UserDTO> = withContext(Dispatchers.IO){
        when(val user = collection.find(Filters.eq(UserDTO::email.name, email)).firstOrNull()){
            null -> Result.failure(NullPointerException("User cannot be found"))
            else -> Result.success(user)
        }
    }

    private companion object {
        const val COLLECTION_NAME = "users"
    }
}