package alexa.execute.domain.repository

import alexa.execute.domain.model.auth.LoginUser
import alexa.execute.domain.model.auth.registerUsers
import alexa.execute.domain.model.auth.toUser
import org.jetbrains.exposed.sql.selectAll
import alexa.execute.domain.model.user.toUser
import alexa.execute.domain.model.user.RespondUserModel
import alexa.execute.domain.model.user.User
import alexa.execute.infrastructure.database.UsersTable
import alexa.execute.infrastructure.services.UserService
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.github.cdimascio.dotenv.Dotenv
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class AuthRepository {
    val dotenv = Dotenv.load()
    val usersService = UserService()

    fun checkIfUserExists(user: User): Boolean {
        var users: List<User> = emptyList()
        transaction {
            users = UsersTable.selectAll().map { it.toUser() }
        }
        var sameEmailUsers: List<User> = users.filter {
            it.email == user.email
        }
        return sameEmailUsers.isEmpty()
    }

    fun createUser(user: User){

//        transaction {
//            UsersTable.insert {
//                it[email] = user.email
//                it[nickname] = user.nickname
//                it[password] = user.password
//                it[age] = user.age
//            }
//        }
    }

    fun loggingUser(loginUser: LoginUser): RespondUserModel? {
        val user = registerUsers.find { it.email == loginUser.email && it.password == loginUser.password }
        return user?.toUser()
    }
}