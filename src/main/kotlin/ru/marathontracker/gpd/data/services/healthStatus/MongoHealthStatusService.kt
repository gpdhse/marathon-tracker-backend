package ru.marathontracker.gpd.data.services.healthStatus

import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import ru.marathontracker.gpd.data.models.dtos.HealthStatusDTO
import ru.marathontracker.gpd.util.ID_FIELD

class MongoHealthStatusService(database: MongoDatabase) : HealthStatusService {
    private val collection = database.getCollection<HealthStatusDTO>("health_statuses")

    override suspend fun getAll(): Result<List<HealthStatusDTO>> = withContext(Dispatchers.IO){
        runCatching {
            collection.find().toList()
        }
    }

    override suspend fun createOrReplace(healthStatus: HealthStatusDTO): Result<Unit> = withContext(Dispatchers.IO) {
        when (collection.find(Filters.eq(ID_FIELD, healthStatus.id)).firstOrNull()) {
            null -> create(healthStatus)
            else -> replace(healthStatus)
        }
    }

    private suspend fun create(healthStatus: HealthStatusDTO): Result<Unit> = withContext(Dispatchers.IO) {
        when (collection.insertOne(healthStatus).insertedId?.asObjectId()?.value?.toHexString()) {
            null -> Result.failure(NullPointerException("Object cannot be inserted"))
            else -> Result.success(Unit)
        }
    }

    private suspend fun replace(healthStatus: HealthStatusDTO): Result<Unit> = withContext(Dispatchers.IO) {
        when(collection.findOneAndReplace(Filters.eq(ID_FIELD, healthStatus.id), healthStatus)){
            null -> Result.failure(NullPointerException("Object cannot be replaced"))
            else -> Result.success(Unit)
        }
    }
}