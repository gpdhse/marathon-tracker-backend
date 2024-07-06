package ru.marathontracker.gpd.plugins

import com.mongodb.kotlin.client.coroutine.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.routing.*
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.ktor.ext.inject
import ru.marathontracker.gpd.authorization.di.*
import ru.marathontracker.gpd.authorization.routes.authorizationRoutes
import ru.marathontracker.gpd.authorization.security.hashing.HashingService
import ru.marathontracker.gpd.authorization.security.token.*
import ru.marathontracker.gpd.authorization.services.RefreshTokenService
import ru.marathontracker.gpd.data.services.user.UserService

data class MongoConfig(
    val user: String? = null,
    val password: String? = null,
    val host: String,
    val port: String,
    val database: String,
)

fun Application.configureRouting() {
    routing {
        val mongo = environment?.config?.let { config ->
            MongoConfig(
                user = config.tryGetString("db.mongo.user"),
                password = config.tryGetString("db.mongo.password"),
                host = config.tryGetString("db.mongo.host") ?: "127.0.0.1",
                port = config.tryGetString("db.mongo.port") ?: "27017",
                database = config.tryGetString("db.mongo.database") ?: "gpd",
            )
        } ?: return@routing

        val credentials =
            mongo.user?.let { userVal -> mongo.password?.let { passwordVal -> "$userVal:$passwordVal@" } }.orEmpty()
        val uri = "mongodb://$credentials${mongo.host}:${mongo.port}/"
        val client by inject<MongoClient> { parametersOf(uri) }
        val database by inject<MongoDatabase> { parametersOf(client, mongo.database) }
        val userService by inject<UserService> { parametersOf(database) }
        val tokenParams by inject<TokenParams> { parametersOf(environment) }
        val accessTokenConfig by inject<TokenConfig>(named(TokenConfigNames.ACCESS)) { parametersOf(tokenParams) }
        val refreshTokenConfig by inject<TokenConfig>(named(TokenConfigNames.REFRESH)) { parametersOf(tokenParams) }
        val tokenService by inject<TokenService>()
        val hashingService by inject<HashingService>()
        val refreshTokenService by inject<RefreshTokenService> { parametersOf(database) }

        authorizationRoutes(
            userService,
            tokenService,
            hashingService,
            refreshTokenService,
            accessTokenConfig,
            refreshTokenConfig,
        )
    }
}
