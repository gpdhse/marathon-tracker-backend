package ru.marathontracker.gpd.authorization.security.token

import com.auth0.jwt.JWT
import java.util.*

internal class JWTTokenService : TokenService {
    override fun generate(config: TokenConfig, vararg claims: TokenClaim): String =
        JWT.create()
            .withAudience(config.audience)
            .withIssuer(config.issuer)
            .withExpiresAt(Date(System.currentTimeMillis() + config.expirationTime))
            .apply { claims.forEach { claim -> withClaim(claim.name, claim.value) } }
            .sign(config.algorithm)
}