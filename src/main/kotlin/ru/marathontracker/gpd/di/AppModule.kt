package ru.marathontracker.gpd.di

import com.mongodb.kotlin.client.coroutine.MongoClient
import org.koin.dsl.module

val appModule = module {
    single { (uri: String) -> MongoClient.create(uri) }
    single { params -> params.get<MongoClient>().getDatabase(params.get()) }
}