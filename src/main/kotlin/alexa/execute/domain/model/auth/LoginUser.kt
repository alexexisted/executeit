package alexa.execute.domain.model.auth

import alexa.execute.domain.model.user.User
import alexa.execute.infrastructure.database.UsersTable
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow

@Serializable
data class LoginUser(
    val email: String,
    val password: String
)
