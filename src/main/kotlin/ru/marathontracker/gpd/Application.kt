package ru.marathontracker.gpd

import io.ktor.server.application.*
import io.ktor.server.netty.*
import ru.marathontracker.gpd.plugins.*

fun main(args: Array<String>) = EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    configureKoin() // important to configure before others configurations

    configureAuthentication()
    configureDefaultHeaders()
    configureMonitoring()
    configureSerialization()
    configureSessions()
    configureSockets() // important to configure before routing
    configureRouting()
}
