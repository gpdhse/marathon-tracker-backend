package ru.marathontracker.gpd.controllers

import io.ktor.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bson.types.ObjectId
import ru.marathontracker.gpd.data.models.dtos.HealthStatusDTO
import ru.marathontracker.gpd.data.services.healthStatus.HealthStatusService
import ru.marathontracker.gpd.models.entities.health.HealthStatus
import ru.marathontracker.gpd.util.MemberAlreadyExistsException
import java.util.concurrent.ConcurrentHashMap

class MarathonController(private val healthStatusService: HealthStatusService) {
    private val members = ConcurrentHashMap<String, Member>()

    fun onJoin(member: Member) {
        if (members.containsKey(member.username)) {
            throw MemberAlreadyExistsException()
        }
        members[member.username] = member
    }

    suspend fun sendStatus(senderUsername: String, status: HealthStatus) {
        members.values.forEach { member ->
            val healthStatusDTO = HealthStatusDTO(
                userId = ObjectId(status.userId),
                maxPressure = status.maxPressure,
                minPressure = status.minPressure,
                avgPressure = status.avgPressure,
                longitude = status.longitude,
                latitude = status.latitude,
                time = status.time,
                speed = status.speed,
                avgSpeed = status.avgSpeed,
                distance = status.distance,
            )

            healthStatusService.createOrReplace(healthStatusDTO).getOrElse {
                return@sendStatus
            }

            val parsedStatus = Json.encodeToString(status)
            member.socket.send(Frame.Text(parsedStatus))
        }
    }

    suspend fun getAllStatuses(): List<HealthStatusDTO> = healthStatusService.getAll().getOrElse { emptyList() }

    suspend fun tryDisconnect(username: String){
        if(members.containsKey(username)){
            members[username]?.socket?.close(CloseReason(CloseReason.Codes.NORMAL, "Disconnected"))
            members.remove(username)
        }
    }
}