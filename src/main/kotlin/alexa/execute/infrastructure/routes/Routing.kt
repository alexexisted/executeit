package alexa.execute.infrastructure.routes

import alexa.execute.domain.repository.AuthRepository
import alexa.execute.infrastructure.routes.authRoutes.authRouting
import alexa.execute.infrastructure.routes.goalRoutes.goalRouting
import alexa.execute.infrastructure.routes.userRoutes.userRouting
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        authRouting(AuthRepository())
        goalRouting()
        userRouting()
    }
}
