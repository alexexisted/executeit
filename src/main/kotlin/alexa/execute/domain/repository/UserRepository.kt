package alexa.execute.domain.repository

import alexa.execute.domain.model.user.User
import alexa.execute.domain.model.user.toUser
import alexa.execute.infrastructure.database.UsersTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class UserRepository {
    fun getAllUsers(): List<User> {
        return transaction {
            UsersTable.selectAll().map { it.toUser() }
        }
    }
}