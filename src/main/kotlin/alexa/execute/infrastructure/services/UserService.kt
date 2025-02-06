package alexa.execute.infrastructure.services

import alexa.execute.domain.model.auth.LoginUser
import alexa.execute.domain.model.user.User
import alexa.execute.domain.model.user.toUser
import alexa.execute.infrastructure.database.UsersTable
import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import io.github.cdimascio.dotenv.Dotenv
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update
import java.util.*

class UserService() {

    val dotenv = Dotenv.load()

    private val jwtSecret = dotenv["JWT_SECRET"] ?: throw IllegalStateException("JWT_SECRET not found in .env")
    // Secret to encryption
    // iss matches jwtDomain
    // aud matches jwtAudience

    private val jwtIssuer = dotenv["ISS"] ?: throw IllegalStateException("ISS not found in .env")
    // Represents the issuer of the token, typically the domain of the service generating the token.(iss)

    private val jwtAudience = dotenv["AUD"] ?: throw IllegalStateException("AUD not found in .env")
    // Specifies the intended recipients of the token.
    // This ensures the token is only valid for applications or services identified by this audience(aud)

    private val tokenExpiryTime = 600_000L
    private val algorithm = Algorithm.HMAC256(jwtSecret)

    val jwtVerifier: JWTVerifier = JWT.require(algorithm)
        .withIssuer(jwtIssuer)
        .withAudience(jwtAudience)
        .build()

    fun createJWT(userEmail: String): String {
        return JWT.create()
            .withIssuer(jwtIssuer)
            .withAudience(jwtAudience)
            .withSubject("Authentication")
            .withClaim("email", userEmail)
            .withExpiresAt(Date(System.currentTimeMillis() + tokenExpiryTime))
            .sign(algorithm)
    }

    fun decodeJWT(token: String): DecodedJWT? {
        return try {
            jwtVerifier.verify(token)
        } catch (e: Exception) {
            null
        }
    }

    fun getEmailFromToken(token: String): String? {
        return decodeJWT(token)?.getClaim("email")?.asString()
    }

    suspend fun create(user: User): Int = dbQuery {
        UsersTable.insert {
            it[email] = user.email
            it[nickname] = user.nickname
            it[password] = user.password
            it[age] = user.age
        }[UsersTable.id]
    }

    suspend fun getUser(id: Int): User? {
        return dbQuery {
            UsersTable.selectAll()
                .where { UsersTable.id eq id }
                .map { it.toUser() }
                .singleOrNull()
        }
    }

    suspend fun getUserByEmail(email: String): User? {
        return dbQuery {
            UsersTable.selectAll()
                .where { UsersTable.email eq email }
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

    suspend fun getAllUsers(): List<User> {
        var users: List<User> = emptyList()
        dbQuery {
            users = UsersTable.selectAll().map { it.toUser() }
        }
        return users
    }

private suspend fun <T> dbQuery(block: suspend () -> T): T =
    newSuspendedTransaction(Dispatchers.IO) { block() }
}

