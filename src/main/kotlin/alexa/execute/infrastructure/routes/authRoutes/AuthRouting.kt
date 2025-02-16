package alexa.execute.infrastructure.routes.authRoutes
import alexa.execute.domain.model.auth.LoginUser
import alexa.execute.domain.model.user.User
import alexa.execute.infrastructure.services.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.authRouting(userService: UserService) {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        post("/register") {
            val userToCreate = call.receive<User>()
            if(userService.checkIfUserExists(userToCreate)) {
                userService.registerUser(userToCreate)
                call.respond(HttpStatusCode.Created, "user ${userToCreate.nickname} created!")
                return@post
            } else {
                call.respond(HttpStatusCode.Conflict, "User already exists!")
                return@post
            }
        }
        post("/login") {
            val credentials = call.receive<LoginUser>()
            val user = userService.loggingUser(credentials)

            if (user == null) {
                call.respond(HttpStatusCode.Unauthorized, "Wrong login or password!")
                return@post
            }
            val token = userService.createJWT(user.email)

            call.response.cookies.append(
                Cookie(
                    name = "token",
                    value = token,
                    httpOnly = true,
                    secure = false,   // TODO(make true on production)
                    maxAge = 60 * 10000 // 10 min exp
                )
            )

            call.respond(mapOf("token" to token))
        }
        post("/logout") {
            call.response.cookies.append(
                Cookie(
                    name = "token",
                    value = "",
                    path = "/",
                    maxAge = 0,
                    httpOnly = true,
                    secure = false  // TODO(make true on production)
                )
            )
            call.respond(HttpStatusCode.OK, mapOf("message" to "Logged out successfully!"))
        }
    }
}