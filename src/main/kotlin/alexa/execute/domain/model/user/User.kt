package alexa.execute.domain.model.user

data class User(
    val email: String,
    val nickname: String,
    val password: String
)

val listUsers = mutableListOf<User>()