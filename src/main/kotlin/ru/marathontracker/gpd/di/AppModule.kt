package ru.marathontracker.gpd.di

import com.mongodb.kotlin.client.coroutine.MongoClient
import org.koin.dsl.module
import ru.marathontracker.gpd.controllers.MarathonController
import ru.marathontracker.gpd.data.services.healthStatus.HealthStatusService

val appModule = module {
    single { (uri: String) -> MongoClient.create(uri) }
    single { (client: MongoClient, database: String) -> client.getDatabase(database) }
    single { (healthStatusService: HealthStatusService) ->
        MarathonController(healthStatusService)
    }
}