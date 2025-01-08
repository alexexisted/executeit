package alexa.execute

import alexa.execute.domain.LoginUser
import alexa.execute.domain.User
import alexa.execute.domain.users
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        post("/register") {
            val user = call.receive<User>()

            //is already exists
            if (users.any { it.email == user.email }) {
                call.respond(HttpStatusCode.Conflict, "User already exists!")
                return@post
            }

            users.add(user)
            call.respond(HttpStatusCode.Created, "User ${user.nickname} was created!")
        }
        post("/login") {
            val credentials = call.receive<LoginUser>()
            val user = users.find { it.email == credentials.email && it.password == credentials.password }
            if (user == null) {
                call.respond(HttpStatusCode.Unauthorized, "Wrong login or password!")
                return@post
            }
            val token = JWT.create()
                .withAudience(System.getenv("AUD") ?: throw IllegalStateException("AUD not set"))
                .withIssuer(System.getenv("ISS") ?: throw IllegalStateException("ISS not set"))
                .withClaim("email", user.email)
                .sign(
                    Algorithm.HMAC256(
                        System.getenv("JWT_SECRET") ?: throw IllegalStateException("JWT_SECRET not set")
                    )
                )

            call.respond(mapOf("token" to token))

        }
        get("/users") {
            val allUsers = users
            call.respond("$allUsers No user found")

        }
    }
}
