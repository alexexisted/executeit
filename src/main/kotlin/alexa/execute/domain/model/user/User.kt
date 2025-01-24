package alexa.execute.domain.model.user

import alexa.execute.domain.model.goal.Goal
import alexa.execute.infrastructure.database.UsersTable
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow

@Serializable
data class User(
    val id: Int? = null,
    val email: String,
    val nickname: String,
    val password: String,
    val age: Int
)

fun ResultRow.toUser(): User {
    return User(
        this[UsersTable.id].toInt(),
        this[UsersTable.email].toString(),
        this[UsersTable.nickname].toString(),
        this[UsersTable.password].toString(),
        this[UsersTable.age].toInt()
    )
}