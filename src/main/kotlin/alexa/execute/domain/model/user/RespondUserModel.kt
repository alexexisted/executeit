package alexa.execute.domain.model.user

data class RespondUserModel(
    val email: String,
    val nickname: String,
    val password: String
)

val listUsers = mutableListOf<RespondUserModel>()