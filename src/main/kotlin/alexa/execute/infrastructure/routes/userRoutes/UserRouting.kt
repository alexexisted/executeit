package alexa.execute.infrastructure.routes.userRoutes

import alexa.execute.infrastructure.services.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.userRouting(userService: UserService) {
    routing {

        authenticate("jwt-auth") {
            route("/user") {

                get("/all") {

                    val users = userService.getAllUsers()
                    call.respond(users)

                }

                get("/profile") {

                    val principal = call.principal<JWTPrincipal>()
                    if (principal == null) {
                        call.respond(HttpStatusCode.Unauthorized, "Token is invalid!")
                        return@get
                    }
                    println(principal)
                    val email = principal?.payload?.getClaim("email")?.asString()

                    if (email == null) {
                        call.respond(HttpStatusCode.Unauthorized, "Token is invalid!")
                        return@get
                    }

                    val user = userService.getUserByEmail(email)
                    if (user != null) {
                        call.respond(user)
                    } else {
                        call.respond("something went wrong! please, try again")
                    }
                }

                post("/create") {

                }

            }
        }
    }
}