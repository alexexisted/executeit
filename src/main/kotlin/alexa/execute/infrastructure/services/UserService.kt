package alexa.execute.infrastructure.services

import alexa.execute.domain.model.auth.LoginUser
import alexa.execute.domain.model.goal.Goal
import alexa.execute.domain.model.goal.toGoal
import alexa.execute.domain.model.user.User
import alexa.execute.domain.model.user.toUser
import alexa.execute.infrastructure.database.GoalsTable
import alexa.execute.infrastructure.database.UsersTable
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import com.auth0.jwt.interfaces.JWTVerifier
import io.github.cdimascio.dotenv.Dotenv
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update
import org.mindrot.jbcrypt.BCrypt
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

    // Hash a password before saving it
    fun hashPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt(7))
    }

    // Verify password when logging in
    fun verifyPassword(plainPassword: String, hashedPassword: String): Boolean {
        return BCrypt.checkpw(plainPassword, hashedPassword)
    }

    suspend fun registerUser(user: User): Int = dbQuery {
        val hashedPassword = hashPassword(user.password)
        UsersTable.insert {
            it[email] = user.email
            it[nickname] = user.nickname
            it[password] = hashedPassword
            it[age] = user.age
        }[UsersTable.id]
    }

    suspend fun createUser(user: User): Int = dbQuery {
        UsersTable.insert {
            it[email] = user.email
            it[nickname] = user.nickname
            it[password] = user.password
            it[age] = user.age
        }[UsersTable.id]
    }

    suspend fun createGoal(goal: Goal): Int = dbQuery {
        GoalsTable.insert {
            it[userId] = goal.userId ?: 0
            it[goalName] = goal.goalName
            it[startDate] = goal.startDate.toString()
            it[endDate] = goal.endDate.toString()
        }[GoalsTable.id]
    }

    suspend fun deleteGoal(goalId: Int): Int = dbQuery {
        GoalsTable.deleteWhere {
            GoalsTable.id.eq(goalId)
        }
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

    suspend fun getUserId(email: String): Int? {
        val user = dbQuery {
            UsersTable.selectAll()
                .where { UsersTable.email eq email }
                .map { it.toUser() }
                .singleOrNull()
        }
        return user?.id
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

    suspend fun checkIfDateClosed(goalId: Int, date: String): Boolean {
        val dateInDb = dbQuery {
            GoalsTable.selectAll()
                .where{GoalsTable.id eq goalId}
                .map { it.toGoal() }
        }
        return ( dateInDb[0].doneDates.contains(date))
    }

    suspend fun addDateToGoal(goalId: Int, date: String) {
        dbQuery {
            GoalsTable.update({ GoalsTable.id eq goalId }) {
                val currentDates = GoalsTable.selectAll()
                    .where { id eq goalId }
                    .map { it[doneDates] }
                    .firstOrNull()

                val updatedDates = if (currentDates.isNullOrEmpty()) {
                    date
                } else {
                    "$currentDates, $date"
                }

                it[doneDates] = updatedDates
            }
        }
    }

    suspend fun delete(id: Int) {
        dbQuery {
            UsersTable.deleteWhere { UsersTable.id.eq(id) }
        }
    }

    suspend fun deleteUser(email: String): Boolean {
        return dbQuery {
            UsersTable.deleteWhere { UsersTable.email.eq(email) } > 0
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
        return if (user != null && verifyPassword(loginUser.password, user!!.password)) {
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

    suspend fun getAllGoals(): List<Goal> {
        return dbQuery {
            GoalsTable.selectAll().map { it.toGoal() }
        }
    }

    suspend fun getUserGoals(userId: Int): List<Goal> {
        return dbQuery {
            GoalsTable.selectAll()
                .where { GoalsTable.userId eq userId }
                .map { it.toGoal() }
        }
    }

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}

