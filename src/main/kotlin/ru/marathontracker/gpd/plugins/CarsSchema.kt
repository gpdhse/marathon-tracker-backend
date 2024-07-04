package ru.marathontracker.gpd.plugins

import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

@Serializable
data class Car(
    val brandName: String,
    val model: String,
    val number: String,
)

class CarService(database: MongoDatabase) {
    private var collection: MongoCollection<Car> = database.getCollection<Car>("cars")

    // Create new car
    suspend fun create(car: Car) = withContext(Dispatchers.IO) {
        collection.insertOne(car).insertedId?.asObjectId()?.value?.toHexString() ?: ""
    }

    // Read a car
    suspend fun read(id: String): Car? = withContext(Dispatchers.IO) {
        collection.find(Filters.eq("_id", ObjectId(id))).firstOrNull()
    }

    // Update a car
    suspend fun update(id: String, car: Car): Car? = withContext(Dispatchers.IO) {
        collection.findOneAndReplace(Filters.eq("_id", ObjectId(id)), car)
    }

    // Delete a car
    suspend fun delete(id: String): Car? = withContext(Dispatchers.IO) {
        collection.findOneAndDelete(Filters.eq("_id", ObjectId(id)))
    }
}

