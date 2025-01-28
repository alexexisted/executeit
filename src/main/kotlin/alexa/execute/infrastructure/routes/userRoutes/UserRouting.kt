package alexa.execute.infrastructure.routes.userRoutes

import alexa.execute.infrastructure.services.UserService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.userRouting(userService: UserService) {
    routing {
        route("/user") {

            get("/all") {
                val users = userService.getAllUsers()
                call.respond(users)

            }

            get("/{id}") {
                val id = call.parameters["id"]
                val user = id?.let { userService.getUser(it.toInt()) }
                if (user != null) {
                    call.respond(user)
                } else {
                    call.respond("something went wrong!")
                }
            }

            post("/create") {

            }

        }
    }
}