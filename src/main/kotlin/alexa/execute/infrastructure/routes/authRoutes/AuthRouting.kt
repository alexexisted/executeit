package alexa.execute.infrastructure.routes.authRoutes
import alexa.execute.domain.model.auth.LoginUser
import alexa.execute.domain.model.auth.RegisterUser
import alexa.execute.domain.model.auth.registerUsers
import alexa.execute.domain.model.user.User
import alexa.execute.domain.repository.AuthRepository
import alexa.execute.infrastructure.services.UserService
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.github.cdimascio.dotenv.Dotenv
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.authRouting(usersService: UserService) {
    val dotenv = Dotenv.load()
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        post("/register") {
            val userToCreate = call.receive<User>()
            if(usersService.checkIfUserExists(userToCreate)) {
                usersService.create(userToCreate)
                call.respond(HttpStatusCode.Created, "user ${userToCreate.nickname} created!")
                return@post
            } else {
                call.respond(HttpStatusCode.Conflict, "User already exists!")
                return@post
            }
        }
        post("/login") {
            val credentials = call.receive<LoginUser>()
            val user = usersService.loggingUser(credentials)

            if (user == null) {
                call.respond(HttpStatusCode.Unauthorized, "Wrong login or password!")
                return@post
            }
            val token = usersService.createJWT(user)
            call.respond(mapOf("token" to token))
        }
    }
}