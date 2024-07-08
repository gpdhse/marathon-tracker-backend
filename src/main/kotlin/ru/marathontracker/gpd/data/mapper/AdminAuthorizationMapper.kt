package ru.marathontracker.gpd.data.mapper

import ru.marathontracker.gpd.authorization.security.hashing.SaltedHash
import ru.marathontracker.gpd.authorization.security.token.TokenClaim
import ru.marathontracker.gpd.data.models.dtos.AdminDTO
import ru.marathontracker.gpd.models.requests.admin.SignUpRequest
import ru.marathontracker.gpd.models.responses.admin.AuthenticateResponse
import ru.marathontracker.gpd.util.*

fun AdminDTO.toAccessTokenClaims(deviceId: String): Array<TokenClaim> {
    return (toClaims(TokenType.ACCESS_TOKEN) + arrayOf(TokenClaim(DEVICE_ID_FIELD, deviceId)))
}

fun AdminDTO.toRefreshTokenClaims(deviceId: String): Array<TokenClaim> {
    return toClaims(TokenType.REFRESH_TOKEN) + arrayOf(TokenClaim(DEVICE_ID_FIELD, deviceId))
}

fun AdminDTO.toClaims(tokenType: TokenType): Array<TokenClaim> {
    return arrayOf(
        TokenClaim("id", id.toHexString()),
        TokenClaim("type", tokenType.toString()),
    )
}

fun AdminDTO.toAuthenticateResponse() = AuthenticateResponse(
    id = id.toHexString(),
    email = email,
    name = name,
)

fun SignUpRequest.toAdminDTO(saltedHash: SaltedHash): AdminDTO = AdminDTO(
    email = email,
    name = name,
    saltedHash = saltedHash
)