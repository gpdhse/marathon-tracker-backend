package ru.marathontracker.gpd.routes.admin

import com.auth0.jwt.JWT
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.marathontracker.gpd.authorization.security.hashing.HashingService
import ru.marathontracker.gpd.authorization.security.token.*
import ru.marathontracker.gpd.data.mapper.*
import ru.marathontracker.gpd.data.models.dtos.*
import ru.marathontracker.gpd.data.services.admin.AdminService
import ru.marathontracker.gpd.data.services.refreshToken.RefreshTokenService
import ru.marathontracker.gpd.models.requests.admin.*
import ru.marathontracker.gpd.models.requests.authorization.RefreshRequest
import ru.marathontracker.gpd.models.responses.authorization.*

fun Routing.adminAuthorizationRoutes(
    adminService: AdminService,
    tokenService: TokenService,
    hashingService: HashingService,
    refreshTokenService: RefreshTokenService,
    accessTokenConfig: TokenConfig,
    refreshTokenConfig: TokenConfig,
) = route("/admin") {
    signIn(adminService, tokenService, hashingService, refreshTokenService, accessTokenConfig, refreshTokenConfig)
    signUp(adminService, tokenService, hashingService, refreshTokenService, accessTokenConfig, refreshTokenConfig)
    authenticate(adminService)
    refresh(adminService, tokenService, refreshTokenService, accessTokenConfig, refreshTokenConfig)
}

private fun Route.signIn(
    adminService: AdminService,
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

    val admin = adminService.findByEmail(email).getOrElse {
        return@post call.respond(HttpStatusCode.Unauthorized)
    }

    if (!hashingService.verify(password, admin.saltedHash)) {
        return@post call.respond(HttpStatusCode.Unauthorized)
    }

    val (accessToken, refreshToken) = makeTokens(
        tokenService,
        accessTokenConfig,
        refreshTokenConfig,
        admin,
        deviceId,
    ).also {
        refreshTokenService.create(RefreshTokenDTO(refreshToken = it[1])).getOrElse {
            return@post call.respond(
                status = HttpStatusCode.InternalServerError,
                message = "Refresh token cannot be saved"
            )
        }
    }

    call.respond(HttpStatusCode.OK, message = SignInResponse(accessToken, refreshToken))
}

private fun Route.signUp(
    adminService: AdminService,
    tokenService: TokenService,
    hashingService: HashingService,
    refreshTokenService: RefreshTokenService,
    accessTokenConfig: TokenConfig,
    refreshTokenConfig: TokenConfig,
) = post("/sign-up") {
    val signUpRequest = runCatching { call.receive<SignUpRequest>() }.getOrElse {
        return@post call.respond(status = HttpStatusCode.BadRequest, message = "Invalid request body")
    }

    val (email, _, password, deviceId) = signUpRequest

    adminService.findByEmail(email).getOrNull()?.let {
        return@post call.respond(status = HttpStatusCode.Conflict, message = "Admin already exists")
    }

    val saltedHash = hashingService.generateSaltedHash(password)
    val admin = signUpRequest.toAdminDTO(saltedHash)

    adminService.create(admin).getOrElse {
        return@post call.respond(status = HttpStatusCode.Conflict, message = "Admin already exists")
    }

    val (accessToken, refreshToken) = makeTokens(
        tokenService,
        accessTokenConfig,
        refreshTokenConfig,
        admin,
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

private fun Route.authenticate(adminService: AdminService) = authenticate {
    get("/authenticate") {
        val principal = call.principal<JWTPrincipal>()!!
        val adminId = principal.getClaim("id", String::class)!!
        val admin = adminService.read(adminId).getOrElse {
            return@get call.respond(HttpStatusCode.NotFound, message = "Admin not found")
        }
        call.respond(status = HttpStatusCode.OK, message = admin.toAuthenticateResponse())
    }
}

private fun Route.refresh(
    adminService: AdminService,
    tokenService: TokenService,
    refreshTokenService: RefreshTokenService,
    accessTokenConfig: TokenConfig,
    refreshTokenConfig: TokenConfig,
) = post("/refresh") {
    val oldRefreshToken =
        runCatching {
            call.receive<RefreshRequest>().token
        }.getOrElse { return@post call.respond(HttpStatusCode.BadRequest, "Invalid refresh token") }

    val token = refreshTokenService.deleteByRefreshToken(oldRefreshToken).getOrElse {
        return@post call.respond(status = HttpStatusCode.NotFound, message = "Refresh token does not exist")
    }.refreshToken

    val jwt = JWT.decode(token)
    val id = runCatching { jwt.getClaim("id").asString() }.getOrElse {
        return@post call.respond(status = HttpStatusCode.InternalServerError, message = "Something went wrong")
    }
    val deviceId = runCatching { jwt.getClaim("device_id").asString() }.getOrElse {
        return@post call.respond(status = HttpStatusCode.InternalServerError, message = "Something went wrong")
    }

    val admin = adminService.read(id).getOrElse {
        return@post call.respond(status = HttpStatusCode.NotFound, message = "Admin not found")
    }

    val (accessToken, refreshToken) = makeTokens(
        tokenService,
        accessTokenConfig,
        refreshTokenConfig,
        admin,
        deviceId
    ).also {
        refreshTokenService.create(RefreshTokenDTO(refreshToken = it[1])).getOrElse {
            return@post call.respond(
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
    admin: AdminDTO,
    deviceId: String,
): List<String> {
    return listOf(
        tokenService.generate(accessTokenConfig, *admin.toAccessTokenClaims(deviceId)),
        tokenService.generate(refreshTokenConfig, *admin.toRefreshTokenClaims(deviceId)),
    )
}