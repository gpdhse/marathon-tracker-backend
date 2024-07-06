package ru.marathontracker.gpd.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import ru.marathontracker.gpd.authorization.security.hashing.HashingService
import ru.marathontracker.gpd.authorization.security.token.TokenConfig

fun Application.configureAuthentication(hashingService: HashingService, tokenConfig: TokenConfig) {
    install(Authentication) {
        jwt {
            realm = "REALM"
            verifier(
                JWT
                    .require(Algorithm.HMAC256(tokenConfig.secret))
                    .withAudience(tokenConfig.audience)
                    .withIssuer(tokenConfig.issuer)
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(tokenConfig.audience))
                    return@validate JWTPrincipal(credential.payload)
                null
            }
        }
    }
}