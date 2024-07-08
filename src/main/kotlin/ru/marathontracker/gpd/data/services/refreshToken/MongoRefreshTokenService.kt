package ru.marathontracker.gpd.data.services.refreshToken

import com.mongodb.client.model.*
import com.mongodb.kotlin.client.coroutine.*
import kotlinx.coroutines.*
import ru.marathontracker.gpd.data.models.dtos.RefreshTokenDTO
import ru.marathontracker.gpd.util.TokenLifetime
import kotlin.time.Duration.Companion.milliseconds

class MongoRefreshTokenService(database: MongoDatabase) : RefreshTokenService {
    private val collection = database.getCollection<RefreshTokenDTO>(COLLECTION_NAME)

    override suspend fun create(refreshToken: RefreshTokenDTO): Result<String> = withContext(Dispatchers.IO) {
        collection.createIndex(
            Indexes.descending("refresh_token"),
            IndexOptions().unique(true).expireAfter(TokenLifetime.REFRESH.milliseconds.inWholeSeconds),
        )
        when (val id = collection.insertOne(refreshToken).insertedId?.asObjectId()?.value?.toHexString()) {
            null -> Result.failure(NullPointerException("Token cannot be inserted"))
            else -> Result.success(id)
        }
    }

    override suspend fun deleteByRefreshToken(refreshToken: String): Result<RefreshTokenDTO> =
        withContext(Dispatchers.IO) {
            when (val oldRefreshToken =
                collection.findOneAndDelete(Filters.eq(RefreshTokenDTO::refreshToken.name, refreshToken))) {
                null -> Result.failure(NullPointerException("Token does not exist"))
                else -> Result.success(oldRefreshToken)
            }
        }

    private companion object {
        private const val COLLECTION_NAME = "refresh_tokens"
    }
}