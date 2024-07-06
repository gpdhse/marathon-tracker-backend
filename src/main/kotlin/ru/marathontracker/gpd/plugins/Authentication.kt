package ru.marathontracker.gpd.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.ktor.ext.inject
import ru.marathontracker.gpd.authorization.di.*
import ru.marathontracker.gpd.authorization.security.token.TokenConfig

fun Application.configureAuthentication() {
    val tokenParams by inject<TokenParams>{ parametersOf(environment) }
    val tokenConfig by inject<TokenConfig>(named(TokenConfigNames.ACCESS)) { parametersOf(tokenParams) }
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