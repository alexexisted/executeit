package alexa.execute.domain.model.goal

import alexa.execute.infrastructure.database.GoalsTable
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow

@Serializable
data class Goal(
    val id: Int? = null,
    var userId: Int? = null,
    val goalName: String,
    val startDate: String?,
    val endDate: String?,
//    val reason: String?,
    val streak: Int = 0,
    val doneDates: List<String?> = listOf(""),
    val attachments: List<String?> = listOf("")
)

fun ResultRow.toGoal() : Goal {
    return Goal(
        this[GoalsTable.id].toInt(),
        this[GoalsTable.userId].toInt(),
        this[GoalsTable.goalName].toString(),
        this[GoalsTable.startDate].toString(),
        this[GoalsTable.endDate].toString(),
        this[GoalsTable.streak].toInt(),
        this[GoalsTable.doneDates]?.split(", ") ?: emptyList(),
    )
}
