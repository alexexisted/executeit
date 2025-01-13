package alexa.execute.domain.model.goal

import kotlinx.serialization.Serializable

@Serializable
data class Goal(
    val id: Int,
    val userId: Int,
    val goalName: String,
    val startDate: String?,
    val endDate: String?,
    val reason: String?,
    val streak: Int,
    val doneDates: List<String>,
    val attachments: List<String>
)
