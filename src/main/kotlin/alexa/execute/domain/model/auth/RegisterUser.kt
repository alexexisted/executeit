package alexa.execute.domain.model.auth

import alexa.execute.domain.model.user.User
import kotlinx.serialization.Serializable

@Serializable
data class RegisterUser(
    val email: String,
    val nickname: String,
    val age: Int,
    val password: String
)

val registerUsers = mutableListOf<RegisterUser>()

fun RegisterUser.toUser() : User {
    return User(
        email = this.email,
        nickname = this.nickname,
        password = this.password
    )
}