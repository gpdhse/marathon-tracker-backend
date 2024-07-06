package ru.marathontracker.gpd.plugins

import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import ru.marathontracker.gpd.authorization.di.authorizationModule
import ru.marathontracker.gpd.data.di.dataModule
import ru.marathontracker.gpd.di.appModule

fun Application.configureKoin() {
    install(Koin) {
        slf4jLogger()
        modules(appModule, dataModule, authorizationModule)
    }
}