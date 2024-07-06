package ru.marathontracker.gpd.authorization.routes

import com.auth0.jwt.JWT
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.marathontracker.gpd.authorization.mapper.*
import ru.marathontracker.gpd.authorization.models.dtos.RefreshTokenDTO
import ru.marathontracker.gpd.authorization.models.requests.*
import ru.marathontracker.gpd.authorization.models.responses.*
import ru.marathontracker.gpd.authorization.security.hashing.HashingService
import ru.marathontracker.gpd.authorization.security.token.*
import ru.marathontracker.gpd.authorization.services.RefreshTokenService
import ru.marathontracker.gpd.data.models.dtos.UserDTO
import ru.marathontracker.gpd.data.services.user.UserService

fun Routing.authorizationRoutes(
    userService: UserService,
    tokenService: TokenService,
    hashingService: HashingService,
    refreshTokenService: RefreshTokenService,
    accessTokenConfig: TokenConfig,
    refreshTokenConfig: TokenConfig,
) {
    route("/authorization") {
        signIn(userService, tokenService, hashingService, refreshTokenService, accessTokenConfig, refreshTokenConfig)
        signUp(userService, tokenService, hashingService, refreshTokenService, accessTokenConfig, refreshTokenConfig)
        authenticate(userService)
        refresh(userService, tokenService, refreshTokenService, accessTokenConfig, refreshTokenConfig)
    }
}

private fun Route.signIn(
    userService: UserService,
    tokenService: TokenService,
    hashingService: HashingService,
    refreshTokenService: RefreshTokenService,
    accessTokenConfig: TokenConfig,
    refreshTokenConfig: TokenConfig,
) = post("/sign-in") {
    val signInRequest = runCatching { call.receive<SignInRequest>() }.getOrElse {
        return@post call.respond(status = HttpStatusCode.BadRequest, message = "Invalid request body")
    }

    val (email, password, deviceId) = signInRequest

    val user = userService.findByEmail(email)
        .getOrElse { return@post call.respond(HttpStatusCode.Unauthorized) }

    if (!hashingService.verify(password, user.saltedHash)) {
        return@post call.respond(HttpStatusCode.Unauthorized)
    }

    val (accessToken, refreshToken) = makeTokens(
        tokenService,
        accessTokenConfig,
        refreshTokenConfig,
        user,
        deviceId
    ).also {
        refreshTokenService.create(RefreshTokenDTO(refreshToken = it[1])).getOrElse {
            return@post call.respond(
                status = HttpStatusCode.InternalServerError,
                message = "Refresh token cannot be saved"
            )
        }
    }

    call.respond(status = HttpStatusCode.OK, message = SignInResponse(accessToken, refreshToken))
}

private fun Route.signUp(
    userService: UserService,
    tokenService: TokenService,
    hashingService: HashingService,
    refreshTokenService: RefreshTokenService,
    accessTokenConfig: TokenConfig,
    refreshTokenConfig: TokenConfig,
) = post("/sign-up") {
    val signUpRequest = runCatching { call.receive<SignUpRequest>() }.getOrElse {
        return@post call.respond(status = HttpStatusCode.BadRequest, message = "Invalid request body")
    }

    val (email, _, _, _, _, _, _, password, deviceId) = signUpRequest

    userService.findByEmail(email).getOrNull()?.let {
        return@post call.respond(status = HttpStatusCode.Conflict, message = "User already exists")
    }

    val saltedHash = hashingService.generateSaltedHash(password)
    val user = signUpRequest.toUserDTO(saltedHash)

    userService.create(user).getOrElse {
        return@post call.respond(status = HttpStatusCode.Conflict, message = "User already exists")
    }

    val (accessToken, refreshToken) = makeTokens(
        tokenService,
        accessTokenConfig,
        refreshTokenConfig,
        user,
        deviceId
    ).also {
        refreshTokenService.create(RefreshTokenDTO(refreshToken = it[1])).getOrElse {
            return@post call.respond(
                status = HttpStatusCode.InternalServerError,
                message = "Refresh token cannot be saved"
            )
        }
    }

    call.respond(status = HttpStatusCode.Created, message = SignUpResponse(accessToken, refreshToken))
}

private fun Route.authenticate(userService: UserService) = authenticate {
    get("/authenticate") {
        val principal = call.principal<JWTPrincipal>()!!
        val userId = principal.getClaim("id", String::class)!!
        val user = userService.read(userId).getOrElse {
            return@get call.respond(HttpStatusCode.NotFound, message = "User not found")
        }
        call.respond(status = HttpStatusCode.OK, message = user.toAuthenticateResponse())
    }
}

private fun Route.refresh(
    userService: UserService,
    tokenService: TokenService,
    refreshTokenService: RefreshTokenService,
    accessTokenConfig: TokenConfig,
    refreshTokenConfig: TokenConfig,
) = get("/refresh") {
    val oldRefreshToken =
        call.parameters["refreshToken"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid refresh token")

    val token = refreshTokenService.delete(oldRefreshToken).getOrElse {
        return@get call.respond(status = HttpStatusCode.NotFound, message = "Refresh token does not exist")
    }.refreshToken

    val jwt = JWT.decode(token)
    val id = runCatching { jwt.getClaim("id").asString() }.getOrElse {
        return@get call.respond(status = HttpStatusCode.InternalServerError, message = "Something went wrong")
    }
    val deviceId = runCatching { jwt.getClaim("deviceId").asString() }.getOrElse {
        return@get call.respond(status = HttpStatusCode.InternalServerError, message = "Something went wrong")
    }

    val user = userService.read(id).getOrElse {
        return@get call.respond(status = HttpStatusCode.NotFound, message = "User not found")
    }

    val (accessToken, refreshToken) = makeTokens(
        tokenService,
        accessTokenConfig,
        refreshTokenConfig,
        user,
        deviceId
    ).also {
        refreshTokenService.create(RefreshTokenDTO(refreshToken = it[1])).getOrElse {
            return@get call.respond(
                status = HttpStatusCode.InternalServerError,
                message = "Refresh token cannot be saved"
            )
        }
    }

    call.respond(status = HttpStatusCode.Created, message = RefreshResponse(accessToken, refreshToken))
}

private fun makeTokens(
    tokenService: TokenService,
    accessTokenConfig: TokenConfig,
    refreshTokenConfig: TokenConfig,
    user: UserDTO,
    deviceId: String,
): List<String> {
    return listOf(
        tokenService.generate(accessTokenConfig, *user.toAccessTokenClaims(deviceId)),
        tokenService.generate(refreshTokenConfig, *user.toRefreshTokenClaims(deviceId)),
    )
}
