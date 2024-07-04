package ru.marathontracker.gpd.routes.user

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.marathontracker.gpd.data.mapper.toUserDTO
import ru.marathontracker.gpd.data.models.requests.CreateUserRequest
import ru.marathontracker.gpd.data.models.requests.UpdateUserRequest
import ru.marathontracker.gpd.data.models.responses.CreateUserResponse
import ru.marathontracker.gpd.data.models.responses.DeleteUserResponse
import ru.marathontracker.gpd.data.models.responses.ReadUserResponse
import ru.marathontracker.gpd.data.models.responses.UpdateUserResponse
import ru.marathontracker.gpd.data.services.UserService

fun Routing.userRoutes(userService: UserService) = route("/user") {
    createUser(userService)
    readUser(userService)
    updateUser(userService)
    deleteUser(userService)
}


private fun Route.createUser(userService: UserService) = post {
    val user = runCatching { call.receive<CreateUserRequest>() }.getOrElse {
        return@post call.respond(status = HttpStatusCode.BadRequest, message = "Invalid request body")
    }
    val id = userService.create(user.toUserDTO()).getOrElse {
        return@post call.respond(status = HttpStatusCode.Conflict, message = "User already exists")
    }
    call.respond(status = HttpStatusCode.Created, message = CreateUserResponse("Success", id))
}

private fun Route.readUser(userService: UserService) = get("/{id}") {
    val userId = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest, message = "No id found")
    val user = userService.read(userId).getOrElse {
        return@get call.respond(status = HttpStatusCode.NotFound, message = "User not found")
    }
    call.respond(status = HttpStatusCode.OK, message = ReadUserResponse("Success", user))
}

private fun Route.updateUser(userService: UserService) = patch("/{id}") {
    val userId = call.parameters["id"] ?: return@patch call.respond(HttpStatusCode.BadRequest, message = "No id found")
    val oldUser = runCatching { call.receive<UpdateUserRequest>() }.getOrElse {
        return@patch call.respond(status = HttpStatusCode.BadRequest, message = "Invalid request body")
    }
    val user = userService.update(userId, oldUser.toUserDTO()).getOrElse {
        return@patch call.respond(status = HttpStatusCode.NotFound, message = "User not found")
    }
    call.respond(status = HttpStatusCode.OK, message = UpdateUserResponse("Success", user))
}

private fun Route.deleteUser(userService: UserService) = delete("/{id}") {
    val userId = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest, message = "No id found")
    val user = userService.delete(userId).getOrElse {
        return@delete call.respond(status = HttpStatusCode.NotFound, message = "User not found")
    }
    call.respond(status = HttpStatusCode.OK, message = DeleteUserResponse("Success", user))
}