package alexa.execute.domain.repository

import alexa.execute.domain.model.user.User
import alexa.execute.infrastructure.database.UsersTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class UserRepository {
    fun getAllUsers(): List<User> {
        return transaction {
            UsersTable.selectAll().map { it.toUser() }
        }
    }

    private fun ResultRow.toUser(): User {
        return User(
            this[UsersTable.id].toInt(),
            this[UsersTable.email].toString(),
            this[UsersTable.nickname].toString(),
            this[UsersTable.password].toString(),
            this[UsersTable.age].toInt()
        )
    }
}