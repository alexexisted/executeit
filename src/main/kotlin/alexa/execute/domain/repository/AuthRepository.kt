package alexa.execute.domain.repository

import alexa.execute.domain.model.auth.LoginUser
import alexa.execute.domain.model.auth.RegisterUser
import alexa.execute.domain.model.auth.registerUsers
import alexa.execute.domain.model.auth.toUser
import alexa.execute.domain.model.user.User
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.github.cdimascio.dotenv.Dotenv

class AuthRepository {
    val dotenv = Dotenv.load()

    fun checkIfUserExists(user: RegisterUser): Boolean {
        return registerUsers.any { it.email == user.email }
    }

    fun loggingUser(loginUser: LoginUser): User? {
        val user = registerUsers.find { it.email == loginUser.email && it.password == loginUser.password }
        return user?.toUser()
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
}