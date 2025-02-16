package alexa.execute.infrastructure.routes

import alexa.execute.infrastructure.routes.authRoutes.authRouting
import alexa.execute.infrastructure.routes.goalRoutes.goalRouting
import alexa.execute.infrastructure.routes.userRoutes.userRouting
import alexa.execute.infrastructure.services.UserService
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting(userService: UserService) {
    routing {
        authRouting(userService)
        goalRouting(userService)
        userRouting(userService)
    }
}
