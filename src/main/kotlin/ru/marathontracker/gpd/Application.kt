package ru.marathontracker.gpd

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import ru.marathontracker.gpd.plugins.*

fun main(args: Array<String>) = EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    configureMonitoring()
    configureRouting()
    configureSockets()
    configureSerialization()
    configureDatabases()
}
