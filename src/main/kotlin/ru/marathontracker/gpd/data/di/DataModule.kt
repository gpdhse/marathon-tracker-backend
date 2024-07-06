package ru.marathontracker.gpd.data.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.*
import ru.marathontracker.gpd.data.services.user.*

val dataModule = module {
    singleOf(::MongoUserService) bind UserService::class
}