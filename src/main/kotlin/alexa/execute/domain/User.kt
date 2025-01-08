package alexa.execute.domain

data class User(
    val email: String,
    val nickname: String,
    val age: Int,
    val password: String
)

data class LoginUser(
    val email: String,
    val password: String
)

val users = mutableListOf<User>()