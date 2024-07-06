package ru.marathontracker.gpd.plugins

import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.routing.*
import org.koin.core.parameter.parametersOf
import org.koin.ktor.ext.inject
import ru.marathontracker.gpd.data.services.user.UserService

fun Application.configureRouting() {
    routing {
        val user = environment?.config?.tryGetString("db.mongo.user")
        val password = environment?.config?.tryGetString("db.mongo.password")
        val host = environment?.config?.tryGetString("db.mongo.host") ?: "127.0.0.1"
        val port = environment?.config?.tryGetString("db.mongo.port") ?: "27017"
        val databaseName = environment?.config?.tryGetString("db.mongo.database.name") ?: "myDatabase"

        val credentials = user?.let { userVal -> password?.let { passwordVal -> "$userVal:$passwordVal@" } }.orEmpty()
        val uri = "mongodb://$credentials$host:$port/"
        val client by inject<MongoClient> { parametersOf(uri) }
        val database by inject<MongoDatabase> { parametersOf(client, databaseName) }
        val userService by inject<UserService> { parametersOf(database) }
    }
}
