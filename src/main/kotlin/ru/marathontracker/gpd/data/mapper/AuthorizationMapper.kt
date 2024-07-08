package ru.marathontracker.gpd.data.mapper

import ru.marathontracker.gpd.authorization.security.hashing.SaltedHash
import ru.marathontracker.gpd.authorization.security.token.TokenClaim
import ru.marathontracker.gpd.data.models.dtos.UserDTO
import ru.marathontracker.gpd.models.requests.authorization.SignUpRequest
import ru.marathontracker.gpd.models.responses.authorization.AuthenticateResponse
import ru.marathontracker.gpd.util.*

fun UserDTO.toAccessTokenClaims(deviceId: String): Array<TokenClaim> {
    return (toClaims(TokenType.ACCESS_TOKEN) + arrayOf(TokenClaim(DEVICE_ID_FIELD, deviceId)))
}

fun UserDTO.toRefreshTokenClaims(deviceId: String): Array<TokenClaim> {
    return toClaims(TokenType.REFRESH_TOKEN) + arrayOf(TokenClaim(DEVICE_ID_FIELD, deviceId))
}

fun UserDTO.toClaims(tokenType: TokenType) : Array<TokenClaim> {
    return arrayOf(
        TokenClaim("id", id.toHexString()),
        TokenClaim("type", tokenType.toString()),
    )
}

fun UserDTO.toAuthenticateResponse() = AuthenticateResponse(
    id = id.toHexString(),
    email = email,
    name = name,
    age = age,
    sex = sex,
    height = height,
    weight = weight,
    phone = phone,
)

fun SignUpRequest.toUserDTO(saltedHash: SaltedHash) = UserDTO(
    email = email,
    name = name,
    age = age,
    sex = sex,
    height = height,
    weight = weight,
    phone = phone,
    saltedHash = saltedHash,
)