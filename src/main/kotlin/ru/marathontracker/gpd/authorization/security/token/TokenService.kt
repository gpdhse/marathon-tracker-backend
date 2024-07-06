package ru.marathontracker.gpd.authorization.security.token

interface TokenService {
    fun generate(
        config: TokenConfig,
        vararg claims: TokenClaim,
    ) : String
}