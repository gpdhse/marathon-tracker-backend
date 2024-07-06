package ru.marathontracker.gpd.authorization.services

import com.mongodb.client.model.*
import com.mongodb.kotlin.client.coroutine.*
import kotlinx.coroutines.*
import org.bson.types.ObjectId
import ru.marathontracker.gpd.authorization.models.dtos.RefreshTokenDTO
import ru.marathontracker.gpd.util.*

class MongoRefreshTokenService(database: MongoDatabase) : RefreshTokenService {
    private val collection = database.getCollection<RefreshTokenDTO>(COLLECTION_NAME)

    override suspend fun create(refreshToken: RefreshTokenDTO): Result<String> = withContext(Dispatchers.IO){
        collection.createIndex(Indexes.descending(RefreshTokenDTO::refreshToken.name), IndexOptions().unique(true).expireAfter(
            TokenLifetime.REFRESH))
        when(val id = collection.insertOne(refreshToken).insertedId?.asObjectId()?.value?.toHexString()){
            null -> Result.failure(NullPointerException("Token cannot be inserted"))
            else -> Result.success(id)
        }
    }

    override suspend fun delete(id: String): Result<RefreshTokenDTO> = withContext(Dispatchers.IO){
        when(val oldRefreshToken = collection.findOneAndDelete(Filters.eq(ID_FIELD, ObjectId(id)))){
            null -> Result.failure(NullPointerException("Token does not exist"))
            else -> Result.success(oldRefreshToken)
        }
    }

    private companion object{
        private const val COLLECTION_NAME = "refresh_tokens"
    }
}