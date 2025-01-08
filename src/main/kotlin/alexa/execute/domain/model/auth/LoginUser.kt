package alexa.execute.domain.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class LoginUser(
    val email: String,
    val password: String
)
