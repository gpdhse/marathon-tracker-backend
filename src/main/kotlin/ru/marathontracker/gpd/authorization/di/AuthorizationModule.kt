package ru.marathontracker.gpd.authorization.di

import io.ktor.server.application.*
import io.ktor.server.config.*
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.*
import ru.marathontracker.gpd.authorization.security.hashing.*
import ru.marathontracker.gpd.authorization.security.token.*
import ru.marathontracker.gpd.util.TokenLifetime

data class TokenParams(
    val issuer: String,
    val audience: String,
    val secret: String,
)

object TokenConfigNames {
    const val REFRESH = "refresh_token"
    const val ACCESS = "access_token"
}

val authorizationModule = module {
    singleOf(::JWTTokenService) bind TokenService::class
    singleOf(::Sha256HashingService) bind HashingService::class

    factory { (environment: ApplicationEnvironment?) ->
        TokenParams(
            issuer = environment?.config?.tryGetString("jwt.issuer") ?: "",
            audience = environment?.config?.tryGetString("jwt.audience") ?: "",
            secret = environment?.config?.tryGetString("jwt.secret") ?: "",
        )
    }

    factory(named(TokenConfigNames.REFRESH)) { (params: TokenParams) ->
        TokenConfig(
            params.issuer,
            params.audience,
            params.secret,
            TokenLifetime.REFRESH,
        )
    }

    factory(named(TokenConfigNames.ACCESS)) { (params: TokenParams) ->
        TokenConfig(
            params.issuer,
            params.audience,
            params.secret,
            TokenLifetime.ACCESS,
        )
    }
}

