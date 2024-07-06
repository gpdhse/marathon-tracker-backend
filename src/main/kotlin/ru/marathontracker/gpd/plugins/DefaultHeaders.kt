package ru.marathontracker.gpd.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.defaultheaders.*

fun Application.configureDefaultHeaders() {
    install(DefaultHeaders)
}