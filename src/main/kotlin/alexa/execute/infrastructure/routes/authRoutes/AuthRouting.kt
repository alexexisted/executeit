package alexa.execute.infrastructure.routes.authRoutes
import alexa.execute.domain.model.auth.LoginUser
import alexa.execute.domain.model.auth.RegisterUser
import alexa.execute.domain.model.auth.registerUsers
import alexa.execute.domain.repository.AuthRepository
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.github.cdimascio.dotenv.Dotenv
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.authRouting(authRepository: AuthRepository) {
    val dotenv = Dotenv.load()
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        post("/register") {
            val registerUser = call.receive<RegisterUser>()

            //is already exists
            if (authRepository.checkIfUserExists(registerUser)) {
                call.respond(HttpStatusCode.Conflict, "User already exists!")
                return@post
            }

            registerUsers.add(registerUser)
            call.respond(HttpStatusCode.Created, "User ${registerUser.nickname} was created!")
        }
        post("/login") {
            val credentials = call.receive<LoginUser>()
            val user = authRepository.loggingUser(credentials)

            if (user == null) {
                call.respond(HttpStatusCode.Unauthorized, "Wrong login or password!")
                return@post
            }
            val token = authRepository.createJWT(user)
            call.respond(mapOf("token" to token))

        }
        get("/users") {
            val allUsers = registerUsers
            call.respond("$allUsers No user found")

        }
    }
}