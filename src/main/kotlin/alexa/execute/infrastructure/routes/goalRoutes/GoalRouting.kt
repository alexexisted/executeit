package alexa.execute.infrastructure.routes.goalRoutes

import alexa.execute.infrastructure.services.UserService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Application.goalRouting(userService: UserService) {
    routing {
        authenticate("jwt-auth") {
            post("/goal/create"){

            }
        }
    }
}