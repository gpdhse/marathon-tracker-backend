package ru.marathontracker.gpd.data.di

import com.mongodb.kotlin.client.coroutine.MongoDatabase
import org.koin.dsl.*
import ru.marathontracker.gpd.data.services.admin.*
import ru.marathontracker.gpd.data.services.refreshToken.*
import ru.marathontracker.gpd.data.services.user.*

val dataModule = module {
    single {(database: MongoDatabase) ->
        MongoUserService(database)
    } bind UserService::class

    single{ (database: MongoDatabase) ->
        MongoAdminService(database)
    } bind AdminService::class

    single { (database: MongoDatabase) ->
        MongoRefreshTokenService(database)
    } bind RefreshTokenService::class
}