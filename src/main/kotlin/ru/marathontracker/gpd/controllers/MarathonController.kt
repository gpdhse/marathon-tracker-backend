package ru.marathontracker.gpd.controllers

import io.ktor.util.collections.*
import io.ktor.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bson.types.ObjectId
import ru.marathontracker.gpd.data.models.dtos.HealthStatusDTO
import ru.marathontracker.gpd.data.services.healthStatus.HealthStatusService
import ru.marathontracker.gpd.models.entities.health.HealthStatus
import ru.marathontracker.gpd.util.MemberAlreadyExistsException

class MarathonController(private val healthStatusService: HealthStatusService) {
    private val members = ConcurrentSet<Member>()

    fun onJoin(member: Member) {
        if (members.find { it.userId == member.userId } != null) {
            throw MemberAlreadyExistsException()
        }
        members.removeIf{ it.userId == member.userId }
        members.add(member)
    }

    suspend fun sendStatus(status: HealthStatus) {
        members.forEach { member ->
            val healthStatusDTO = HealthStatusDTO(
                userId = ObjectId(status.userId),
                pulse = status.pulse,
                latitude = status.latitude,
                longitude = status.longitude,
            )

            healthStatusService.createOrReplace(healthStatusDTO).getOrElse {
                return@sendStatus
            }

            val parsedStatus = Json.encodeToString(status)
            member.socket.send(Frame.Text(parsedStatus))
        }
    }

    suspend fun getAllStatuses(): List<HealthStatusDTO> = healthStatusService.getAll().getOrElse { emptyList() }

    suspend fun tryDisconnect(userId: String){
        members.find { it.userId == userId }?.let {
            it.socket.close(CloseReason(CloseReason.Codes.NORMAL, "Disconnected"))
            members.remove(it)
        }
    }
}