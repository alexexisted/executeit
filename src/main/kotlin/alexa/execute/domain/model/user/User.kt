package alexa.execute.domain.model.user

import alexa.execute.domain.model.goal.Goal
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val email: String,
    val nickname: String,
    val password: String,
    val age: Int
)