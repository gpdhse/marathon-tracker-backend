package ru.marathontracker.gpd.data.di

import com.mongodb.kotlin.client.coroutine.MongoDatabase
import org.koin.dsl.bind
import org.koin.dsl.module
import ru.marathontracker.gpd.data.services.MongoUserService
import ru.marathontracker.gpd.data.services.UserService

val dataModule = module {
    single{ (database : MongoDatabase) -> MongoUserService(database) } bind UserService::class
}