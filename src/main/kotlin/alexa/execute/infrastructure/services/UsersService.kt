package alexa.execute.infrastructure.services

import alexa.execute.domain.model.auth.LoginUser
import alexa.execute.domain.model.user.RespondUserModel
import alexa.execute.domain.model.user.User
import alexa.execute.domain.model.user.toUser
import alexa.execute.infrastructure.database.UsersTable
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.github.cdimascio.dotenv.Dotenv
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update

class UserService() {

    val dotenv = Dotenv.load()

    suspend fun create(user: User): Int = dbQuery {
        UsersTable.insert {
            it[email] = user.email
            it[nickname] = user.nickname
            it[password] = user.password
            it[age] = user.age
        }[UsersTable.id]
    }

    suspend fun read(id: Int): User? {
        return dbQuery {
            UsersTable.selectAll()
                .where { UsersTable.id eq id }
                .map { it.toUser() }
                .singleOrNull()
        }
    }

    suspend fun update(id: Int, user: User) {
        dbQuery {
            UsersTable.update({ UsersTable.id eq id }) {
                it[email] = user.email
                it[nickname] = user.nickname
                it[password] = user.password
                it[age] = user.age
            }
        }
    }

    suspend fun delete(id: Int) {
        dbQuery {
            UsersTable.deleteWhere { UsersTable.id.eq(id) }
        }
    }

    suspend fun checkIfUserExists(user: User): Boolean {
        var users: List<User> = emptyList()
        dbQuery {
            users = UsersTable.selectAll().map { it.toUser() }
        }
        var sameEmailUsers: List<User> = users.filter {
            it.email == user.email
        }
        return sameEmailUsers.isEmpty()
    }

    suspend fun loggingUser(loginUser: LoginUser): User? {
        var user: User? = null
        dbQuery {
            user = UsersTable.selectAll()
                .where { UsersTable.email eq loginUser.email }
                .map { it.toUser() }
                .singleOrNull()
        }
        return if (user?.password == loginUser.password) {
            user
        } else {
            null
        }
    }

    fun createJWT(user: User): String {
        val token = JWT.create()
            .withAudience(dotenv["AUD"] ?: throw IllegalStateException("AUD not set"))
            .withIssuer(dotenv["ISS"] ?: throw IllegalStateException("ISS not set"))
            .withClaim("email", user.email)
            .sign(
                Algorithm.HMAC256(
                    dotenv["JWT_SECRET"] ?: throw IllegalStateException("JWT_SECRET not set")
                )
            )
        return token
    }

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}

