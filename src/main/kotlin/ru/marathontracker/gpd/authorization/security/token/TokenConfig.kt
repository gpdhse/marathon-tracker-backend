package ru.marathontracker.gpd.authorization.security.token

import com.auth0.jwt.algorithms.Algorithm

data class TokenConfig(
    val issuer: String,
    val audience: String,
    val secret: String,
    val expirationTime: Long,
    val algorithm: Algorithm = Algorithm.HMAC256(secret)
)
